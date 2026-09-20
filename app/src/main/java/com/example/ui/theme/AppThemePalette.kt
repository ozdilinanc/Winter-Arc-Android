package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppThemeId(val key: String) {
    FOREST_PINE("forest_pine"),
    NORDIC_FROST("nordic_frost"),
    WARM_ESPRESSO("warm_espresso"),
    GITHUB_DIMMED("github_dimmed"),
    DRACULA_VELVET("dracula_velvet"),
    OLED_PITCH("oled_pitch"),
    OBSIDIAN_INDIGO("obsidian_indigo"),
    LIGHT_PAPER("light_paper"),
    CREAM_PARCHMENT("cream_parchment"),
    CYBER_NEON("cyber_neon"),
    MATRIX_TERMINAL("matrix_terminal"),
    SOLARIZED_DARK("solarized_dark"),
    SAKURA_NIGHT("sakura_night")
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
    val branchGraduation: Color,
    val isLight: Boolean = false
) {
    val previewSwatches: List<Color>
        get() = listOf(canvasDark, panelNavyElevated, accentCyan, textPrimary)

    fun toDarkColorScheme(): ColorScheme = toColorScheme()

    fun toColorScheme(): ColorScheme = if (isLight) {
        lightColorScheme(
            primary = accentCyan,
            onPrimary = Color.White,
            primaryContainer = accentCyan.copy(alpha = 0.15f),
            onPrimaryContainer = accentCyan,
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
    } else {
        darkColorScheme(
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
    }

    companion object {
        // 1. FOREST PINE (Derin Çam Ormanı & Adaçayı) - Göz Rahatlatıcı Yeşil
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
            textPrimary = Color(0xFFE6EFEA),
            textSecondary = Color(0xFFA4B8AD),
            textMuted = Color(0xFF6B8074),
            textDarkMuted = Color(0xFF4B5A52),
            textAccent = Color(0xFF34D399),
            branchCS = Color(0xFF6EE7B7),
            branchDotNet = Color(0xFF34D399),
            branchDevOps = Color(0xFF5EEAD4),
            branchAndroid = Color(0xFF10B981),
            branchTools = Color(0xFF2DD4BF),
            branchSecurity = Color(0xFFFBBF24),
            branchEnglish = Color(0xFFA7F3D0),
            branchPortfolio = Color(0xFF34D399),
            branchKnowledge = Color(0xFF6EE7B7),
            branchCerts = Color(0xFFA4B8AD),
            branchLanguage = Color(0xFFE6EFEA),
            branchGraduation = Color(0xFFF87171)
        )

        // 2. NORDIC FROST (İskandinav Buzulu & Arktik Lacivert) - Soğuk Mavi
        val NordicFrost = AppThemePalette(
            id = AppThemeId.NORDIC_FROST,
            name = "Nordic Frost",
            emoji = "❄️",
            subtitle = "İskandinav Gece Mavisi",
            description = "Düşük mavi ışıklı, parlamayan buzul turkuaz ve derin arktik lacivert.",
            canvasDark = Color(0xFF0E1318),
            panelNavy = Color(0xFF151C24),
            panelNavyElevated = Color(0xFF1C2530),
            panelNavyHighlight = Color(0xFF24303E),
            borderSubtle = Color(0xFF263342),
            borderActive = Color(0xFF3B4F66),
            accentCyan = Color(0xFF38BDF8),        // Soft Ice Sky
            accentCyanGlow = Color(0x3338BDF8),
            accentIndigo = Color(0xFF60A5FA),
            accentPurple = Color(0xFF818CF8),
            accentViolet = Color(0xFFA5B4FC),
            accentAmber = Color(0xFFF59E0B),
            accentGold = Color(0xFFFCD34D),
            accentEmerald = Color(0xFF34D399),
            textPrimary = Color(0xFFE2E8F0),
            textSecondary = Color(0xFF94A3B8),
            textMuted = Color(0xFF64748B),
            textDarkMuted = Color(0xFF475569),
            textAccent = Color(0xFF38BDF8),
            branchCS = Color(0xFF818CF8),
            branchDotNet = Color(0xFF38BDF8),
            branchDevOps = Color(0xFF60A5FA),
            branchAndroid = Color(0xFF34D399),
            branchTools = Color(0xFF38BDF8),
            branchSecurity = Color(0xFFF59E0B),
            branchEnglish = Color(0xFF93C5FD),
            branchPortfolio = Color(0xFF38BDF8),
            branchKnowledge = Color(0xFF818CF8),
            branchCerts = Color(0xFF94A3B8),
            branchLanguage = Color(0xFFCBD5E1),
            branchGraduation = Color(0xFFFB7185)
        )

        // 3. WARM ESPRESSO (Sıcak Karamel & Kömür) - Mavi Işıksız Sıcak Kahve
        val WarmEspresso = AppThemePalette(
            id = AppThemeId.WARM_ESPRESSO,
            name = "Warm Espresso",
            emoji = "☕",
            subtitle = "Sıcak Karamel & Kömür",
            description = "Mavi ışıktan arındırılmış, gece geç saatler ve kitap okuma için dinlendirici sıcak kahve tonları.",
            canvasDark = Color(0xFF141110),
            panelNavy = Color(0xFF1C1816),
            panelNavyElevated = Color(0xFF25201D),
            panelNavyHighlight = Color(0xFF302A26),
            borderSubtle = Color(0xFF38302B),
            borderActive = Color(0xFF4E433C),
            accentCyan = Color(0xFFF59E0B),        // Warm Amber / Caramel
            accentCyanGlow = Color(0x33F59E0B),
            accentIndigo = Color(0xFFD97706),
            accentPurple = Color(0xFFFBBF24),
            accentViolet = Color(0xFFFDE68A),
            accentAmber = Color(0xFFF59E0B),
            accentGold = Color(0xFFFCD34D),
            accentEmerald = Color(0xFF10B981),
            textPrimary = Color(0xFFEDE4DC),
            textSecondary = Color(0xFFB8A99C),
            textMuted = Color(0xFF85766A),
            textDarkMuted = Color(0xFF5E534A),
            textAccent = Color(0xFFF59E0B),
            branchCS = Color(0xFFD97706),
            branchDotNet = Color(0xFFF59E0B),
            branchDevOps = Color(0xFFFBBF24),
            branchAndroid = Color(0xFF10B981),
            branchTools = Color(0xFFB45309),
            branchSecurity = Color(0xFFF59E0B),
            branchEnglish = Color(0xFFFDE68A),
            branchPortfolio = Color(0xFFF59E0B),
            branchKnowledge = Color(0xFFD97706),
            branchCerts = Color(0xFFB8A99C),
            branchLanguage = Color(0xFFEDE4DC),
            branchGraduation = Color(0xFFEF4444)
        )

        // 4. GITHUB DIMMED (Mühendis Füme & Slate) - Dengeli Nötr
        val GitHubDimmed = AppThemePalette(
            id = AppThemeId.GITHUB_DIMMED,
            name = "GitHub Dimmed",
            emoji = "🐙",
            subtitle = "Kurumsal Füme & Slate",
            description = "GitHub'ın göz yormayan, dünya çapında mühendislerin tercih ettiği dengeli koyu gri.",
            canvasDark = Color(0xFF161B22),
            panelNavy = Color(0xFF21262D),
            panelNavyElevated = Color(0xFF29303A),
            panelNavyHighlight = Color(0xFF333B47),
            borderSubtle = Color(0xFF30363D),
            borderActive = Color(0xFF484F58),
            accentCyan = Color(0xFF58A6FF),        // GitHub Blue
            accentCyanGlow = Color(0x3358A6FF),
            accentIndigo = Color(0xFF79C0FF),
            accentPurple = Color(0xFFBC8CFF),
            accentViolet = Color(0xFFD2A8FF),
            accentAmber = Color(0xFFD29922),
            accentGold = Color(0xFFE3B341),
            accentEmerald = Color(0xFF3FB950),
            textPrimary = Color(0xFFE6EDF3),
            textSecondary = Color(0xFF8B949E),
            textMuted = Color(0xFF6E7681),
            textDarkMuted = Color(0xFF484F58),
            textAccent = Color(0xFF58A6FF),
            branchCS = Color(0xFFBC8CFF),
            branchDotNet = Color(0xFF58A6FF),
            branchDevOps = Color(0xFF79C0FF),
            branchAndroid = Color(0xFF3FB950),
            branchTools = Color(0xFF58A6FF),
            branchSecurity = Color(0xFFD29922),
            branchEnglish = Color(0xFF79C0FF),
            branchPortfolio = Color(0xFF3FB950),
            branchKnowledge = Color(0xFFBC8CFF),
            branchCerts = Color(0xFF8B949E),
            branchLanguage = Color(0xFFE6EDF3),
            branchGraduation = Color(0xFFF85149)
        )

        // 5. DRACULA VELVET (Pastel Kadife Mor) - Mor & Lila
        val DraculaVelvet = AppThemePalette(
            id = AppThemeId.DRACULA_VELVET,
            name = "Dracula Velvet",
            emoji = "🦇",
            subtitle = "Pastel Kadife Mor",
            description = "Parlama yapmayan yumuşak pastel leylak aksanları ve mat koyu kadife paneller.",
            canvasDark = Color(0xFF1E1F29),
            panelNavy = Color(0xFF282A36),
            panelNavyElevated = Color(0xFF343746),
            panelNavyHighlight = Color(0xFF44475A),
            borderSubtle = Color(0xFF44475A),
            borderActive = Color(0xFF6272A4),
            accentCyan = Color(0xFFBD93F9),        // Dracula Purple
            accentCyanGlow = Color(0x33BD93F9),
            accentIndigo = Color(0xFF8BE9FD),      // Cyan
            accentPurple = Color(0xFFFF79C6),      // Pink
            accentViolet = Color(0xFFBD93F9),
            accentAmber = Color(0xFFFFB86C),      // Orange
            accentGold = Color(0xFFF1FA8C),        // Yellow
            accentEmerald = Color(0xFF50FA7B),     // Green
            textPrimary = Color(0xFFF8F8F2),
            textSecondary = Color(0xFFBFBFBF),
            textMuted = Color(0xFF6272A4),
            textDarkMuted = Color(0xFF44475A),
            textAccent = Color(0xFFBD93F9),
            branchCS = Color(0xFFFF79C6),
            branchDotNet = Color(0xFFBD93F9),
            branchDevOps = Color(0xFF8BE9FD),
            branchAndroid = Color(0xFF50FA7B),
            branchTools = Color(0xFF8BE9FD),
            branchSecurity = Color(0xFFFFB86C),
            branchEnglish = Color(0xFFF1FA8C),
            branchPortfolio = Color(0xFF50FA7B),
            branchKnowledge = Color(0xFFBD93F9),
            branchCerts = Color(0xFF6272A4),
            branchLanguage = Color(0xFFF8F8F2),
            branchGraduation = Color(0xFFFF5555)
        )

        // 6. OLED PURE PITCH (Saf AMOLED Siyah) - Sıfır Işık & Yüksek Kontrast
        val OledPitch = AppThemePalette(
            id = AppThemeId.OLED_PITCH,
            name = "OLED Pure Pitch",
            emoji = "🖤",
            subtitle = "Saf Amoled & Titanyum",
            description = "Tamamen kapalı siyah pikseller, sıfır arka ışık parlaması ve mat titanyum detaylar.",
            canvasDark = Color(0xFF000000),
            panelNavy = Color(0xFF0A0A0A),
            panelNavyElevated = Color(0xFF141414),
            panelNavyHighlight = Color(0xFF1F1F1F),
            borderSubtle = Color(0xFF262626),
            borderActive = Color(0xFF404040),
            accentCyan = Color(0xFF94A3B8),        // Titanium Silver
            accentCyanGlow = Color(0x3394A3B8),
            accentIndigo = Color(0xFFCBD5E1),
            accentPurple = Color(0xFFE2E8F0),
            accentViolet = Color(0xFFF1F5F9),
            accentAmber = Color(0xFFF59E0B),
            accentGold = Color(0xFFFBBF24),
            accentEmerald = Color(0xFF10B981),
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFFA1A1AA),
            textMuted = Color(0xFF71717A),
            textDarkMuted = Color(0xFF52525B),
            textAccent = Color(0xFFF8FAFC),
            branchCS = Color(0xFFCBD5E1),
            branchDotNet = Color(0xFF94A3B8),
            branchDevOps = Color(0xFFA1A1AA),
            branchAndroid = Color(0xFF10B981),
            branchTools = Color(0xFF71717A),
            branchSecurity = Color(0xFFF59E0B),
            branchEnglish = Color(0xFFE2E8F0),
            branchPortfolio = Color(0xFF10B981),
            branchKnowledge = Color(0xFF94A3B8),
            branchCerts = Color(0xFF52525B),
            branchLanguage = Color(0xFFF8FAFC),
            branchGraduation = Color(0xFFEF4444)
        )

        // 7. OBSIDIAN INDIGO (Klasik Winter Arc) - Özgün İndigo
        val ObsidianIndigo = AppThemePalette(
            id = AppThemeId.OBSIDIAN_INDIGO,
            name = "Obsidian Indigo",
            emoji = "🌌",
            subtitle = "Klasik Winter Arc",
            description = "Mevcut orijinal temanın göz kamaştırmayan, kontrastı dengelenmiş rafine hali.",
            canvasDark = Color(0xFF080B10),
            panelNavy = Color(0xFF0F172A),
            panelNavyElevated = Color(0xFF1E293B),
            panelNavyHighlight = Color(0xFF283548),
            borderSubtle = Color(0xFF1E293B),
            borderActive = Color(0xFF334155),
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

        // 8. CLEAN LIGHT (Açık Kağıt & Minimalist Beyaz) - Açık Tema
        val LightPaper = AppThemePalette(
            id = AppThemeId.LIGHT_PAPER,
            name = "Clean Light",
            emoji = "☀️",
            subtitle = "Açık Kağıt & Minimal",
            description = "Gündüz ve aydınlık ortamlarda maksimum netlik sağlayan ferah açık tema.",
            canvasDark = Color(0xFFF8FAFC),
            panelNavy = Color(0xFFFFFFFF),
            panelNavyElevated = Color(0xFFF1F5F9),
            panelNavyHighlight = Color(0xFFE2E8F0),
            borderSubtle = Color(0xFFE2E8F0),
            borderActive = Color(0xFFCBD5E1),
            accentCyan = Color(0xFF2563EB),        // Royal Blue
            accentCyanGlow = Color(0x332563EB),
            accentIndigo = Color(0xFF4F46E5),
            accentPurple = Color(0xFF9333EA),
            accentViolet = Color(0xFF7C3AED),
            accentAmber = Color(0xFFD97706),
            accentGold = Color(0xFFB45309),
            accentEmerald = Color(0xFF059669),
            textPrimary = Color(0xFF0F172A),       // Ink Slate-900
            textSecondary = Color(0xFF475569),     // Slate-600
            textMuted = Color(0xFF64748B),         // Slate-500
            textDarkMuted = Color(0xFF94A3B8),     // Slate-400
            textAccent = Color(0xFF2563EB),
            branchCS = Color(0xFF7C3AED),
            branchDotNet = Color(0xFF2563EB),
            branchDevOps = Color(0xFF4F46E5),
            branchAndroid = Color(0xFF059669),
            branchTools = Color(0xFF0284C7),
            branchSecurity = Color(0xFFD97706),
            branchEnglish = Color(0xFF0891B2),
            branchPortfolio = Color(0xFF059669),
            branchKnowledge = Color(0xFF4F46E5),
            branchCerts = Color(0xFF64748B),
            branchLanguage = Color(0xFF0F172A),
            branchGraduation = Color(0xFFE11D48),
            isLight = true
        )

        // 9. CREAM & SEPIA (Krem Parşömen & Kitap Modu) - Açık Sıcak Tema
        val CreamParchment = AppThemePalette(
            id = AppThemeId.CREAM_PARCHMENT,
            name = "Krem & Sepya",
            emoji = "📜",
            subtitle = "Sıcak Parşömen & Kitap",
            description = "Gözü dinlendiren yumuşak krem sepya zemin ve koyu kavrulmuş kahve mürekkep tonları.",
            canvasDark = Color(0xFFF4ECD8),
            panelNavy = Color(0xFFFAF4E8),
            panelNavyElevated = Color(0xFFEFE6D0),
            panelNavyHighlight = Color(0xFFE5DAC2),
            borderSubtle = Color(0xFFDFD2BA),
            borderActive = Color(0xFFC7B696),
            accentCyan = Color(0xFFB45309),        // Warm Cinnamon Amber
            accentCyanGlow = Color(0x33B45309),
            accentIndigo = Color(0xFF92400E),
            accentPurple = Color(0xFF78350F),
            accentViolet = Color(0xFF854D0E),
            accentAmber = Color(0xFFD97706),
            accentGold = Color(0xFFCA8A04),
            accentEmerald = Color(0xFF2D7D46),
            textPrimary = Color(0xFF2B2118),       // Roasted Bean Ink
            textSecondary = Color(0xFF5A4B3C),
            textMuted = Color(0xFF857361),
            textDarkMuted = Color(0xFFA89785),
            textAccent = Color(0xFFB45309),
            branchCS = Color(0xFF78350F),
            branchDotNet = Color(0xFFB45309),
            branchDevOps = Color(0xFF92400E),
            branchAndroid = Color(0xFF2D7D46),
            branchTools = Color(0xFF854D0E),
            branchSecurity = Color(0xFFD97706),
            branchEnglish = Color(0xFFB45309),
            branchPortfolio = Color(0xFF2D7D46),
            branchKnowledge = Color(0xFF92400E),
            branchCerts = Color(0xFF857361),
            branchLanguage = Color(0xFF2B2118),
            branchGraduation = Color(0xFF991B1B),
            isLight = true
        )

        // 10. CYBERPUNK NEON (Gece Şehri & Elektrik Cyan) - Ultra Canlı Koyu
        val CyberNeon = AppThemePalette(
            id = AppThemeId.CYBER_NEON,
            name = "Cyberpunk Neon",
            emoji = "⚡",
            subtitle = "Elektrik Gece & Neon Cyan",
            description = "Karanlık gece moru zemin üzerinde parlayan elektrik cyan ve sıcak pembe aksanlar.",
            canvasDark = Color(0xFF0C0A17),
            panelNavy = Color(0xFF161328),
            panelNavyElevated = Color(0xFF211D3B),
            panelNavyHighlight = Color(0xFF2D2750),
            borderSubtle = Color(0xFF3F356B),
            borderActive = Color(0xFF00F0FF),
            accentCyan = Color(0xFF00F0FF),        // Electric Cyan
            accentCyanGlow = Color(0x5500F0FF),
            accentIndigo = Color(0xFFFF0055),      // Hot Neon Rose
            accentPurple = Color(0xFFA855F7),
            accentViolet = Color(0xFFE879F9),
            accentAmber = Color(0xFFFFE600),      // Cyber Yellow
            accentGold = Color(0xFFFFD000),
            accentEmerald = Color(0xFF00FF9F),     // Toxic Lime
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFD8B4FE),
            textMuted = Color(0xFF9370DB),
            textDarkMuted = Color(0xFF6B52A3),
            textAccent = Color(0xFF00F0FF),
            branchCS = Color(0xFFE879F9),
            branchDotNet = Color(0xFF00F0FF),
            branchDevOps = Color(0xFFA855F7),
            branchAndroid = Color(0xFF00FF9F),
            branchTools = Color(0xFF00F0FF),
            branchSecurity = Color(0xFFFFE600),
            branchEnglish = Color(0xFFFF0055),
            branchPortfolio = Color(0xFF00FF9F),
            branchKnowledge = Color(0xFFE879F9),
            branchCerts = Color(0xFF9370DB),
            branchLanguage = Color(0xFFFFFFFF),
            branchGraduation = Color(0xFFFF0055)
        )

        // 11. MATRIX TERMINAL (Retro CRT Hacker) - Monokrom Yeşil
        val MatrixTerminal = AppThemePalette(
            id = AppThemeId.MATRIX_TERMINAL,
            name = "Matrix Hacker",
            emoji = "📟",
            subtitle = "CRT Fosfor Yeşili",
            description = "Klasik hacker terminali hissi veren simsiyah zemin ve fosforlu monokrom yeşil ışıklar.",
            canvasDark = Color(0xFF050B05),
            panelNavy = Color(0xFF0A150A),
            panelNavyElevated = Color(0xFF102210),
            panelNavyHighlight = Color(0xFF163016),
            borderSubtle = Color(0xFF1D421D),
            borderActive = Color(0xFF22C55E),
            accentCyan = Color(0xFF22C55E),        // Phosphor Green
            accentCyanGlow = Color(0x4422C55E),
            accentIndigo = Color(0xFF4ADE80),
            accentPurple = Color(0xFF86EFAC),
            accentViolet = Color(0xFF86EFAC),
            accentAmber = Color(0xFFEAB308),
            accentGold = Color(0xFFFACC15),
            accentEmerald = Color(0xFF22C55E),
            textPrimary = Color(0xFFF0FDF4),
            textSecondary = Color(0xFF86EFAC),
            textMuted = Color(0xFF4ADE80),
            textDarkMuted = Color(0xFF166534),
            textAccent = Color(0xFF22C55E),
            branchCS = Color(0xFF86EFAC),
            branchDotNet = Color(0xFF22C55E),
            branchDevOps = Color(0xFF4ADE80),
            branchAndroid = Color(0xFF22C55E),
            branchTools = Color(0xFF4ADE80),
            branchSecurity = Color(0xFFEAB308),
            branchEnglish = Color(0xFF86EFAC),
            branchPortfolio = Color(0xFF22C55E),
            branchKnowledge = Color(0xFF86EFAC),
            branchCerts = Color(0xFF4ADE80),
            branchLanguage = Color(0xFFF0FDF4),
            branchGraduation = Color(0xFFEF4444)
        )

        // 12. SOLARIZED DARK (Petrol Mavisi & Kehribar) - İkonik Palet
        val SolarizedDark = AppThemePalette(
            id = AppThemeId.SOLARIZED_DARK,
            name = "Solarized Dark",
            emoji = "🌅",
            subtitle = "Derin Petrol & Kehribar",
            description = "Geliştiricilerin ikonik renk sistemi: Petrol mavisi zemin, sıcak kehribar ve cyan detaylar.",
            canvasDark = Color(0xFF002B36),
            panelNavy = Color(0xFF073642),
            panelNavyElevated = Color(0xFF0E4654),
            panelNavyHighlight = Color(0xFF155666),
            borderSubtle = Color(0xFF1D6678),
            borderActive = Color(0xFF2AA198),
            accentCyan = Color(0xFF2AA198),        // Solarized Cyan
            accentCyanGlow = Color(0x332AA198),
            accentIndigo = Color(0xFF268BD2),      // Blue
            accentPurple = Color(0xFF6C71C4),      // Violet
            accentViolet = Color(0xFF6C71C4),
            accentAmber = Color(0xFFB58900),      // Yellow
            accentGold = Color(0xFFCB4B16),        // Orange
            accentEmerald = Color(0xFF859900),     // Green
            textPrimary = Color(0xFFFDF6E3),       // Base3
            textSecondary = Color(0xFF93A1A1),     // Base1
            textMuted = Color(0xFF657B83),         // Base00
            textDarkMuted = Color(0xFF586E75),     // Base01
            textAccent = Color(0xFF2AA198),
            branchCS = Color(0xFF6C71C4),
            branchDotNet = Color(0xFF2AA198),
            branchDevOps = Color(0xFF268BD2),
            branchAndroid = Color(0xFF859900),
            branchTools = Color(0xFF2AA198),
            branchSecurity = Color(0xFFB58900),
            branchEnglish = Color(0xFF268BD2),
            branchPortfolio = Color(0xFF859900),
            branchKnowledge = Color(0xFF6C71C4),
            branchCerts = Color(0xFF657B83),
            branchLanguage = Color(0xFFFDF6E3),
            branchGraduation = Color(0xFFDC322F)
        )

        // 13. SAKURA NIGHT (Mürdüm Kadife & Gül Pembesi) - Pastel Zarafet
        val SakuraNight = AppThemePalette(
            id = AppThemeId.SAKURA_NIGHT,
            name = "Sakura Gece",
            emoji = "🌸",
            subtitle = "Mürdüm & Gül Pembesi",
            description = "Derin mürdüm kadife zemin üzerinde pastel sakura çiçeği ve yumuşak lavanta tonları.",
            canvasDark = Color(0xFF17111A),
            panelNavy = Color(0xFF231926),
            panelNavyElevated = Color(0xFF302234),
            panelNavyHighlight = Color(0xFF3F2C44),
            borderSubtle = Color(0xFF4F3656),
            borderActive = Color(0xFFF472B6),
            accentCyan = Color(0xFFF472B6),        // Sakura Rose
            accentCyanGlow = Color(0x33F472B6),
            accentIndigo = Color(0xFFE879F9),      // Orchid
            accentPurple = Color(0xFFC084FC),
            accentViolet = Color(0xFFE879F9),
            accentAmber = Color(0xFFFB7185),      // Coral Rose
            accentGold = Color(0xFFFDA4AF),
            accentEmerald = Color(0xFF34D399),
            textPrimary = Color(0xFFFDF2F8),
            textSecondary = Color(0xFFF5D0FE),
            textMuted = Color(0xFFA882B5),
            textDarkMuted = Color(0xFF75517F),
            textAccent = Color(0xFFF472B6),
            branchCS = Color(0xFFC084FC),
            branchDotNet = Color(0xFFF472B6),
            branchDevOps = Color(0xFFE879F9),
            branchAndroid = Color(0xFF34D399),
            branchTools = Color(0xFFFB7185),
            branchSecurity = Color(0xFFFDA4AF),
            branchEnglish = Color(0xFFF472B6),
            branchPortfolio = Color(0xFF34D399),
            branchKnowledge = Color(0xFFC084FC),
            branchCerts = Color(0xFFA882B5),
            branchLanguage = Color(0xFFFDF2F8),
            branchGraduation = Color(0xFFFB7185)
        )

        val allPalettes = listOf(
            ForestPine,
            NordicFrost,
            WarmEspresso,
            GitHubDimmed,
            DraculaVelvet,
            OledPitch,
            ObsidianIndigo,
            LightPaper,
            CreamParchment,
            CyberNeon,
            MatrixTerminal,
            SolarizedDark,
            SakuraNight
        )

        fun fromId(id: AppThemeId): AppThemePalette = when (id) {
            AppThemeId.FOREST_PINE -> ForestPine
            AppThemeId.NORDIC_FROST -> NordicFrost
            AppThemeId.WARM_ESPRESSO -> WarmEspresso
            AppThemeId.GITHUB_DIMMED -> GitHubDimmed
            AppThemeId.DRACULA_VELVET -> DraculaVelvet
            AppThemeId.OLED_PITCH -> OledPitch
            AppThemeId.OBSIDIAN_INDIGO -> ObsidianIndigo
            AppThemeId.LIGHT_PAPER -> LightPaper
            AppThemeId.CREAM_PARCHMENT -> CreamParchment
            AppThemeId.CYBER_NEON -> CyberNeon
            AppThemeId.MATRIX_TERMINAL -> MatrixTerminal
            AppThemeId.SOLARIZED_DARK -> SolarizedDark
            AppThemeId.SAKURA_NIGHT -> SakuraNight
        }

        fun fromKey(key: String): AppThemePalette {
            val matched = allPalettes.find { it.id.key == key }
            return matched ?: ForestPine
        }
    }
}
