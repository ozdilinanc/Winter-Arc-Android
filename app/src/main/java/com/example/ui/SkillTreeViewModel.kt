package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.SkillTreeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class SkillDashboardViewMode(val label: String, val iconEmoji: String) {
    TREE_MAP("Ağaç", "🌳"),
    CATEGORIES("Kategoriler", "🗂️"),
    DAILY_TRACKER("Günlük", "📅"),
    PROGRESS_ANALYTICS("İlerleme", "📊")
}

data class SkillTreeUiState(
    val allSkills: List<SkillNode> = emptyList(),
    val filteredSkills: List<SkillNode> = emptyList(),
    val projects: List<EngineeringProject> = emptyList(),
    val selectedSkill: SkillNode? = null,
    val searchQuery: String = "",
    val selectedBranchFilter: BranchId? = null,
    val selectedStatusFilter: SkillStatus? = null,
    val currentViewMode: SkillDashboardViewMode = SkillDashboardViewMode.CATEGORIES,
    val isFilterSheetVisible: Boolean = false,
    val isAddProjectDialogOpen: Boolean = false,
    val userXp: UserXpProfile = UserXpProfile(),
    val activeRewardNotification: RewardNotification? = null,
    val isAchievementsDialogOpen: Boolean = false,
    val isDailyBannerDismissed: Boolean = false,
    val dailyFocusSkillIndex: Int = 0
) {
    val totalCount: Int get() = allSkills.size
    val notStartedCount: Int get() = allSkills.count { it.status == SkillStatus.NOT_STARTED }
    val inProgressCount: Int get() = allSkills.count { it.status == SkillStatus.IN_PROGRESS }
    val learningCount: Int get() = inProgressCount
    val practicedCount: Int get() = allSkills.count { it.status == SkillStatus.PRACTICED }
    val completedCount: Int get() = allSkills.count { it.status == SkillStatus.COMPLETED }
    val strongCount: Int get() = allSkills.count { it.status == SkillStatus.STRONG }

    val inProgressSkills: List<SkillNode>
        get() = allSkills.filter { it.status == SkillStatus.IN_PROGRESS }

    val currentDailyFocusSkill: SkillNode?
        get() {
            val list = inProgressSkills
            if (list.isNotEmpty()) {
                val safeIndex = dailyFocusSkillIndex.coerceIn(0, list.lastIndex)
                return list[safeIndex]
            }
            return allSkills.firstOrNull { it.status == SkillStatus.NOT_STARTED }
        }

    val masteryPercent: Int get() {
        if (allSkills.isEmpty()) return 0
        val mastered = allSkills.count { it.status == SkillStatus.COMPLETED || it.status == SkillStatus.STRONG }
        return ((mastered.toFloat() / allSkills.size) * 100).toInt()
    }

    val backendMasteryPercent: Int get() {
        val backendSkills = allSkills.filter { it.branchId == BranchId.BACKEND_DOTNET }
        if (backendSkills.isEmpty()) return 0
        val mastered = backendSkills.count { it.status == SkillStatus.COMPLETED || it.status == SkillStatus.STRONG }
        return ((mastered.toFloat() / backendSkills.size) * 100).toInt()
    }
}

class SkillTreeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SkillTreeRepository(AppDatabase.getDatabase(application), context = application)

    private val _searchQuery = MutableStateFlow("")
    private val _selectedBranch = MutableStateFlow<BranchId?>(null)
    private val _selectedStatus = MutableStateFlow<SkillStatus?>(null)
    private val _selectedSkillId = MutableStateFlow<String?>(null)
    private val _currentViewMode = MutableStateFlow(SkillDashboardViewMode.TREE_MAP)
    private val _isAddProjectDialogOpen = MutableStateFlow(false)
    private val _activeRewardNotification = MutableStateFlow<RewardNotification?>(null)
    private val _isAchievementsDialogOpen = MutableStateFlow(false)
    private val _isDailyBannerDismissed = MutableStateFlow(false)
    private val _dailyFocusSkillIndex = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            repository.rewardNotificationFlow.collect { reward ->
                _activeRewardNotification.value = reward
            }
        }
    }

    private data class FilterParams(
        val query: String,
        val branch: BranchId?,
        val status: SkillStatus?,
        val selectedId: String?,
        val viewMode: SkillDashboardViewMode
    )

    private val filtersFlow: Flow<FilterParams> = combine(
        _searchQuery,
        _selectedBranch,
        _selectedStatus,
        _selectedSkillId,
        _currentViewMode
    ) { query, branch, status, selectedId, viewMode ->
        FilterParams(query, branch, status, selectedId, viewMode)
    }

    private data class DialogState(
        val isAddProject: Boolean,
        val reward: RewardNotification?,
        val isAchievements: Boolean,
        val isBannerDismissed: Boolean,
        val bannerSkillIndex: Int
    )

    private val dialogStatesFlow: Flow<DialogState> = combine(
        _isAddProjectDialogOpen,
        _activeRewardNotification,
        _isAchievementsDialogOpen,
        _isDailyBannerDismissed,
        _dailyFocusSkillIndex
    ) { addProject, reward, achievements, bannerDismissed, skillIndex ->
        DialogState(addProject, reward, achievements, bannerDismissed, skillIndex)
    }

    val uiState: StateFlow<SkillTreeUiState> = combine(
        repository.skillsFlow,
        repository.projectsFlow,
        repository.userXpFlow,
        filtersFlow,
        dialogStatesFlow
    ) { skills, projects, userXp, filters, dialogs ->
        val filtered = skills.filter { skill ->
            val matchesQuery = filters.query.isBlank() ||
                    skill.name.contains(filters.query, ignoreCase = true) ||
                    skill.category.contains(filters.query, ignoreCase = true) ||
                    skill.description.contains(filters.query, ignoreCase = true) ||
                    skill.branchId.title.contains(filters.query, ignoreCase = true)
            val matchesBranch = filters.branch == null || skill.branchId == filters.branch
            val matchesStatus = filters.status == null || skill.status == filters.status

            matchesQuery && matchesBranch && matchesStatus
        }

        val currentSelected = skills.find { it.id == filters.selectedId }

        SkillTreeUiState(
            allSkills = skills,
            filteredSkills = filtered,
            projects = projects,
            selectedSkill = currentSelected,
            searchQuery = filters.query,
            selectedBranchFilter = filters.branch,
            selectedStatusFilter = filters.status,
            currentViewMode = filters.viewMode,
            isAddProjectDialogOpen = dialogs.isAddProject,
            userXp = userXp,
            activeRewardNotification = dialogs.reward,
            isAchievementsDialogOpen = dialogs.isAchievements,
            isDailyBannerDismissed = dialogs.isBannerDismissed,
            dailyFocusSkillIndex = dialogs.bannerSkillIndex
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SkillTreeUiState()
    )


    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSelectBranchFilter(branch: BranchId?) {
        _selectedBranch.value = if (_selectedBranch.value == branch) null else branch
    }

    fun onSelectStatusFilter(status: SkillStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun onSelectSkill(skill: SkillNode?) {
        _selectedSkillId.value = skill?.id
    }

    fun onSetViewMode(viewMode: SkillDashboardViewMode) {
        _currentViewMode.value = viewMode
    }

    fun onUpdateSkillStatus(skillId: String, newStatus: SkillStatus) {
        viewModelScope.launch {
            repository.updateSkillStatus(skillId, newStatus)
        }
    }

    fun onCycleSkillStatus(skillId: String) {
        viewModelScope.launch {
            repository.cycleSkillStatus(skillId)
        }
    }

    fun onSaveSkillNotes(skillId: String, notes: String) {
        viewModelScope.launch {
            repository.updateSkillNotes(skillId, notes)
        }
    }

    fun onAddResource(skillId: String, resourceUrl: String) {
        viewModelScope.launch {
            repository.addCustomResource(skillId, resourceUrl)
        }
    }

    fun onAddProjectToSkill(skillId: String, projectName: String) {
        viewModelScope.launch {
            repository.addCustomProjectToSkill(skillId, projectName)
        }
    }

    fun setAddProjectDialogVisible(visible: Boolean) {
        _isAddProjectDialogOpen.value = visible
    }

    fun createProject(
        title: String,
        category: String,
        description: String,
        stage: ProjectWorkflowStage,
        github: String,
        mediumUrl: String,
        notes: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            val project = EngineeringProject(
                id = "proj_${UUID.randomUUID().toString().take(8)}",
                title = title,
                description = description,
                category = category,
                currentStage = stage,
                githubRepo = github,
                mediumArticleUrl = mediumUrl,
                notes = notes,
                tags = tags
            )
            repository.saveProject(project)
            _isAddProjectDialogOpen.value = false
        }
    }

    fun advanceProjectStage(projectId: String, newStage: ProjectWorkflowStage) {
        viewModelScope.launch {
            val currentList = uiState.value.projects
            val found = currentList.find { it.id == projectId }
            if (found != null) {
                repository.saveProject(found.copy(currentStage = newStage))
            }
        }
    }

    fun regressProjectStage(projectId: String) {
        viewModelScope.launch {
            val currentList = uiState.value.projects
            val found = currentList.find { it.id == projectId }
            if (found != null) {
                val stages = ProjectWorkflowStage.values()
                val currentIndex = found.currentStage.order - 1
                if (currentIndex > 0) {
                    val prevStage = stages[currentIndex - 1]
                    repository.saveProject(found.copy(currentStage = prevStage))
                }
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    fun dismissRewardNotification() {
        _activeRewardNotification.value = null
    }

    fun setAchievementsDialogVisible(visible: Boolean) {
        _isAchievementsDialogOpen.value = visible
    }

    fun dismissDailyBanner() {
        _isDailyBannerDismissed.value = true
    }

    fun showDailyBanner() {
        _isDailyBannerDismissed.value = false
    }

    fun cycleNextDailyFocusSkill() {
        val inProgressList = uiState.value.inProgressSkills
        if (inProgressList.isNotEmpty()) {
            _dailyFocusSkillIndex.value = (_dailyFocusSkillIndex.value + 1) % inProgressList.size
        }
    }
}

