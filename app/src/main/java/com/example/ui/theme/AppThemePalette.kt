package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

enum class AppThemeId(val key: String) {
    FOREST_PINE("forest_pine"),
    NORDIC_FROST("nordic_frost"),
    WARM_ESPRESSO("warm_espresso"),
    GITHUB_DIMMED("github_dimmed"),
    DRACULA_VELVET("dracula_velvet"),
    OLED_PITCH("oled_pitch"),
    OBSIDIAN_INDIGO("obsidian_indigo")
}

data class AppThemePalette(
    val id: AppThemeId,
    val name: String,
    val emoji: String,
    val subtitle: String,
    val description: String,
    val canvasDark: Color,
    val panelNavy: Color,
    val panelNavyElevated: Color,
    val panelNavyHighlight: Color,
    val borderSubtle: Color,
    val borderActive: Color,
    val accentCyan: Color,         // Primary accent color
    val accentCyanGlow: Color,
    val accentIndigo: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentAmber: Color,
    val accentGold: Color,
    val accentEmerald: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textDarkMuted: Color,
    val textAccent: Color,
    val branchCS: Color,
    val branchDotNet: Color,
    val branchDevOps: Color,
    val branchAndroid: Color,
    val branchTools: Color,
    val branchSecurity: Color,
    val branchEnglish: Color,
    val branchPortfolio: Color,
    val branchKnowledge: Color,
    val branchCerts: Color,
    val branchLanguage: Color,
    val branchGraduation: Color
) {
    val previewSwatches: List<Color>
        get() = listOf(canvasDark, panelNavyElevated, accentCyan, textPrimary)

    fun toDarkColorScheme(): ColorScheme = darkColorScheme(
        primary = accentCyan,
        onPrimary = Color.White,
        primaryContainer = accentCyan.copy(alpha = 0.2f),
        onPrimaryContainer = textPrimary,
        secondary = accentIndigo,
        onSecondary = Color.White,
        secondaryContainer = panelNavyElevated,
        onSecondaryContainer = textSecondary,
        tertiary = accentPurple,
        onTertiary = Color.White,
        background = canvasDark,
        onBackground = textPrimary,
        surface = panelNavy,
        onSurface = textPrimary,
        surfaceVariant = panelNavyElevated,
        onSurfaceVariant = textSecondary,
        outline = borderSubtle,
        outlineVariant = borderActive
    )

    companion object {
        // 1. FOREST PINE (Derin Çam Ormanı & Adaçayı) - Maksimum Göz Rahatlığı
        val ForestPine = AppThemePalette(
            id = AppThemeId.FOREST_PINE,
            name = "Forest Pine",
            emoji = "🌲",
            subtitle = "Derin Çam & Adaçayı",
            description = "Gece kodlamasında gözü en az yoran, parlamayan doğal çam ve adaçayı yeşili tonları.",
            canvasDark = Color(0xFF0F1412),
            panelNavy = Color(0xFF171F1B),
            panelNavyElevated = Color(0xFF1F2924),
            panelNavyHighlight = Color(0xFF283630),
            borderSubtle = Color(0xFF2B3A33),
            borderActive = Color(0xFF3B5047),
            accentCyan = Color(0xFF34D399),        // Mint / Sage
            accentCyanGlow = Color(0x3334D399),
            accentIndigo = Color(0xFF5EEAD4),      // Soft Teal
            accentPurple = Color(0xFFA7F3D0),
            accentViolet = Color(0xFF86EFAC),
            accentAmber = Color(0xFFFBBF24),
            accentGold = Color(0xFFFCD34D),
            accentEmerald = Color(0xFF10B981),
            textPrimary = Color(0xFFF1F5F3),
            textSecondary = Color(0xFF9CA3AF),
            textMuted = Color(0xFF6B7280),
            textDarkMuted = Color(0xFF4B5563),
            textAccent = Color(0xFF34D399),
            branchCS = Color(0xFF86EFAC),
            branchDotNet = Color(0xFF2DD4BF),
            branchDevOps = Color(0xFF6EE7B7),
            branchAndroid = Color(0xFF34D399),
            branchTools = Color(0xFF5EEAD4),
            branchSecurity = Color(0xFFFBBF24),
            branchEnglish = Color(0xFF6EE7B7),
            branchPortfolio = Color(0xFF10B981),
            branchKnowledge = Color(0xFF34D399),
            branchCerts = Color(0xFF9CA3AF),
            branchLanguage = Color(0xFFE5E7EB),
            branchGraduation = Color(0xFFF87171)
        )

        // 2. NORDIC FROST (İskandinav Gece Mavisi & Buzul)
        val NordicFrost = AppThemePalette(
            id = AppThemeId.NORDIC_FROST,
            name = "Nordic Frost",
            emoji = "❄️",
            subtitle = "İskandinav Gece Mavisi",
            description = "Düşük mavi ışıklı, parlamayan buzul turkuaz ve derin arktik lacivert.",
            canvasDark = Color(0xFF141820),
            panelNavy = Color(0xFF1C222E),
            panelNavyElevated = Color(0xFF242C3C),
            panelNavyHighlight = Color(0xFF2F394D),
            borderSubtle = Color(0xFF2F3A4E),
            borderActive = Color(0xFF435370),
            accentCyan = Color(0xFF88C0D0),        // Nord Frost Blue
            accentCyanGlow = Color(0x3388C0D0),
            accentIndigo = Color(0xFF81A1C1),
            accentPurple = Color(0xFFB48EAD),
            accentViolet = Color(0xFFD8DEE9),
            accentAmber = Color(0xFFEBCB8B),
            accentGold = Color(0xFFD08770),
            accentEmerald = Color(0xFFA3BE8C),
            textPrimary = Color(0xFFECEFF4),
            textSecondary = Color(0xFFD8DEE9),
            textMuted = Color(0xFF8892B0),
            textDarkMuted = Color(0xFF5C6378),
            textAccent = Color(0xFF88C0D0),
            branchCS = Color(0xFFB48EAD),
            branchDotNet = Color(0xFF81A1C1),
            branchDevOps = Color(0xFF88C0D0),
            branchAndroid = Color(0xFFA3BE8C),
            branchTools = Color(0xFF88C0D0),
            branchSecurity = Color(0xFFEBCB8B),
            branchEnglish = Color(0xFF81A1C1),
            branchPortfolio = Color(0xFFA3BE8C),
            branchKnowledge = Color(0xFF88C0D0),
            branchCerts = Color(0xFF8892B0),
            branchLanguage = Color(0xFFECEFF4),
            branchGraduation = Color(0xFFBF616A)
        )

        // 3. WARM ESPRESSO (Sıcak Karamel & Füme Kömür) - Sıfır Mavi Işık
        val WarmEspresso = AppThemePalette(
            id = AppThemeId.WARM_ESPRESSO,
            name = "Warm Espresso",
            emoji = "☕",
            subtitle = "Sıcak Karamel & Kömür",
            description = "Mavi ışıktan arındırılmış, gece geç saatler ve kitap okuma için dinlendirici sıcak kahve tonları.",
            canvasDark = Color(0xFF141211),
            panelNavy = Color(0xFF1E1A18),
            panelNavyElevated = Color(0xFF292421),
            panelNavyHighlight = Color(0xFF38312C),
            borderSubtle = Color(0xFF38312C),
            borderActive = Color(0xFF4F453E),
            accentCyan = Color(0xFFF59E0B),        // Warm Amber
            accentCyanGlow = Color(0x33F59E0B),
            accentIndigo = Color(0xFFE0A96D),      // Caramel Cream
            accentPurple = Color(0xFFD97706),
            accentViolet = Color(0xFFFCD34D),
            accentAmber = Color(0xFFF59E0B),
            accentGold = Color(0xFFFBBF24),
            accentEmerald = Color(0xFF84A98C),
            textPrimary = Color(0xFFF5EBE6),
            textSecondary = Color(0xFFD6C7C2),
            textMuted = Color(0xFF9E8E87),
            textDarkMuted = Color(0xFF6B5E57),
            textAccent = Color(0xFFE0A96D),
            branchCS = Color(0xFFE0A96D),
            branchDotNet = Color(0xFFF59E0B),
            branchDevOps = Color(0xFFD97706),
            branchAndroid = Color(0xFF84A98C),
            branchTools = Color(0xFFE0A96D),
            branchSecurity = Color(0xFFF59E0B),
            branchEnglish = Color(0xFFE0A96D),
            branchPortfolio = Color(0xFF84A98C),
            branchKnowledge = Color(0xFFF59E0B),
            branchCerts = Color(0xFF9E8E87),
            branchLanguage = Color(0xFFF5EBE6),
            branchGraduation = Color(0xFFE07A5F)
        )

        // 4. GITHUB DIMMED (Kurumsal Füme & Slate)
        val GitHubDimmed = AppThemePalette(
            id = AppThemeId.GITHUB_DIMMED,
            name = "GitHub Dimmed",
            emoji = "🐙",
            subtitle = "Kurumsal Füme & Slate",
            description = "GitHub'ın göz yormayan, dünya çapında mühendislerin tercih ettiği dengeli koyu gri.",
            canvasDark = Color(0xFF161B22),
            panelNavy = Color(0xFF21262D),
            panelNavyElevated = Color(0xFF292E36),
            panelNavyHighlight = Color(0xFF343A45),
            borderSubtle = Color(0xFF30363D),
            borderActive = Color(0xFF484F58),
            accentCyan = Color(0xFF539BF5),        // GitHub Blue
            accentCyanGlow = Color(0x33539BF5),
            accentIndigo = Color(0xFF6CB6FF),
            accentPurple = Color(0xFFBC8CFF),
            accentViolet = Color(0xFFD2A8FF),
            accentAmber = Color(0xFFD29922),
            accentGold = Color(0xFFE3B341),
            accentEmerald = Color(0xFF3FB950),
            textPrimary = Color(0xFFADBAC7),
            textSecondary = Color(0xFF768390),
            textMuted = Color(0xFF636E7B),
            textDarkMuted = Color(0xFF444C56),
            textAccent = Color(0xFF539BF5),
            branchCS = Color(0xFFBC8CFF),
            branchDotNet = Color(0xFF539BF5),
            branchDevOps = Color(0xFF6CB6FF),
            branchAndroid = Color(0xFF3FB950),
            branchTools = Color(0xFF539BF5),
            branchSecurity = Color(0xFFD29922),
            branchEnglish = Color(0xFF6CB6FF),
            branchPortfolio = Color(0xFF3FB950),
            branchKnowledge = Color(0xFF539BF5),
            branchCerts = Color(0xFF768390),
            branchLanguage = Color(0xFFADBAC7),
            branchGraduation = Color(0xFFF85149)
        )

        // 5. DRACULA VELVET (Pastel Gece Moru)
        val DraculaVelvet = AppThemePalette(
            id = AppThemeId.DRACULA_VELVET,
            name = "Dracula Velvet",
            emoji = "🦇",
            subtitle = "Pastel Kadife Mor",
            description = "Parlama yapmayan yumuşak pastel leylak aksanları ve mat koyu kadife paneller.",
            canvasDark = Color(0xFF191A21),
            panelNavy = Color(0xFF21222C),
            panelNavyElevated = Color(0xFF282A36),
            panelNavyHighlight = Color(0xFF343746),
            borderSubtle = Color(0xFF343746),
            borderActive = Color(0xFF44475A),
            accentCyan = Color(0xFFBD93F9),        // Dracula Purple
            accentCyanGlow = Color(0x33BD93F9),
            accentIndigo = Color(0xFF8BE9FD),      // Cyan
            accentPurple = Color(0xFFFF79C6),      // Pink
            accentViolet = Color(0xFFBD93F9),
            accentAmber = Color(0xFFF1FA8C),
            accentGold = Color(0xFFFFB86C),
            accentEmerald = Color(0xFF50FA7B),
            textPrimary = Color(0xFFF8F8F2),
            textSecondary = Color(0xFFBFBFBF),
            textMuted = Color(0xFF80859D),
            textDarkMuted = Color(0xFF6272A4),
            textAccent = Color(0xFFBD93F9),
            branchCS = Color(0xFFBD93F9),
            branchDotNet = Color(0xFF8BE9FD),
            branchDevOps = Color(0xFFFF79C6),
            branchAndroid = Color(0xFF50FA7B),
            branchTools = Color(0xFF8BE9FD),
            branchSecurity = Color(0xFFFFB86C),
            branchEnglish = Color(0xFF8BE9FD),
            branchPortfolio = Color(0xFF50FA7B),
            branchKnowledge = Color(0xFFBD93F9),
            branchCerts = Color(0xFF80859D),
            branchLanguage = Color(0xFFF8F8F2),
            branchGraduation = Color(0xFFFF5555)
        )

        // 6. OLED PURE PITCH (Amoled Minimal Siyah & Titanyum)
        val OledPitch = AppThemePalette(
            id = AppThemeId.OLED_PITCH,
            name = "OLED Pure Pitch",
            emoji = "🖤",
            subtitle = "Saf Amoled & Titanyum",
            description = "Tamamen kapalı siyah pikseller, sıfır arka ışık parlaması ve mat titanyum detaylar.",
            canvasDark = Color(0xFF000000),
            panelNavy = Color(0xFF0D0D10),
            panelNavyElevated = Color(0xFF141418),
            panelNavyHighlight = Color(0xFF1F1F24),
            borderSubtle = Color(0xFF222228),
            borderActive = Color(0xFF383842),
            accentCyan = Color(0xFF94A3B8),        // Titanium Silver
            accentCyanGlow = Color(0x3394A3B8),
            accentIndigo = Color(0xFFCBD5E1),
            accentPurple = Color(0xFFE2E8F0),
            accentViolet = Color(0xFFF8FAFC),
            accentAmber = Color(0xFFF59E0B),
            accentGold = Color(0xFFFBBF24),
            accentEmerald = Color(0xFF10B981),
            textPrimary = Color(0xFFF1F5F9),
            textSecondary = Color(0xFF94A3B8),
            textMuted = Color(0xFF64748B),
            textDarkMuted = Color(0xFF475569),
            textAccent = Color(0xFFCBD5E1),
            branchCS = Color(0xFFCBD5E1),
            branchDotNet = Color(0xFF94A3B8),
            branchDevOps = Color(0xFF64748B),
            branchAndroid = Color(0xFF10B981),
            branchTools = Color(0xFF94A3B8),
            branchSecurity = Color(0xFFF59E0B),
            branchEnglish = Color(0xFFCBD5E1),
            branchPortfolio = Color(0xFF10B981),
            branchKnowledge = Color(0xFF94A3B8),
            branchCerts = Color(0xFF64748B),
            branchLanguage = Color(0xFFF1F5F9),
            branchGraduation = Color(0xFFEF4444)
        )

        // 7. OBSIDIAN INDIGO (Klasik Winter Arc - Optimize Edilmiş)
        val ObsidianIndigo = AppThemePalette(
            id = AppThemeId.OBSIDIAN_INDIGO,
            name = "Obsidian Indigo",
            emoji = "🌌",
            subtitle = "Klasik Winter Arc",
            description = "Mevcut orijinal temanın göz kamaştırmayan, kontrastı dengelenmiş rafine hali.",
            canvasDark = Color(0xFF07090E),
            panelNavy = Color(0xFF0F131C),
            panelNavyElevated = Color(0xFF151B27),
            panelNavyHighlight = Color(0xFF20293C),
            borderSubtle = Color(0xFF212B3E),
            borderActive = Color(0xFF33425E),
            accentCyan = Color(0xFF6366F1),        // Indigo
            accentCyanGlow = Color(0x336366F1),
            accentIndigo = Color(0xFF818CF8),
            accentPurple = Color(0xFFA855F7),
            accentViolet = Color(0xFFC084FC),
            accentAmber = Color(0xFFF59E0B),
            accentGold = Color(0xFFFBBF24),
            accentEmerald = Color(0xFF10B981),
            textPrimary = Color(0xFFF1F5F9),
            textSecondary = Color(0xFF94A3B8),
            textMuted = Color(0xFF64748B),
            textDarkMuted = Color(0xFF475569),
            textAccent = Color(0xFF818CF8),
            branchCS = Color(0xFFC084FC),
            branchDotNet = Color(0xFF818CF8),
            branchDevOps = Color(0xFFA78BFA),
            branchAndroid = Color(0xFF10B981),
            branchTools = Color(0xFF38BDF8),
            branchSecurity = Color(0xFFF59E0B),
            branchEnglish = Color(0xFF38BDF8),
            branchPortfolio = Color(0xFF10B981),
            branchKnowledge = Color(0xFF818CF8),
            branchCerts = Color(0xFF94A3B8),
            branchLanguage = Color(0xFFE2E8F0),
            branchGraduation = Color(0xFFF43F5E)
        )

        val allPalettes = listOf(
            ForestPine,
            NordicFrost,
            WarmEspresso,
            GitHubDimmed,
            DraculaVelvet,
            OledPitch,
            ObsidianIndigo
        )

        fun fromId(id: AppThemeId): AppThemePalette = when (id) {
            AppThemeId.FOREST_PINE -> ForestPine
            AppThemeId.NORDIC_FROST -> NordicFrost
            AppThemeId.WARM_ESPRESSO -> WarmEspresso
            AppThemeId.GITHUB_DIMMED -> GitHubDimmed
            AppThemeId.DRACULA_VELVET -> DraculaVelvet
            AppThemeId.OLED_PITCH -> OledPitch
            AppThemeId.OBSIDIAN_INDIGO -> ObsidianIndigo
        }

        fun fromKey(key: String): AppThemePalette {
            val matched = allPalettes.find { it.id.key == key }
            return matched ?: ForestPine
        }
    }
}
