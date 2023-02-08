package com.dangerfield.core.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.dangerfield.notable.designsystem.Blue10
import com.dangerfield.notable.designsystem.Blue20
import com.dangerfield.notable.designsystem.Blue30
import com.dangerfield.notable.designsystem.Blue40
import com.dangerfield.notable.designsystem.Blue80
import com.dangerfield.notable.designsystem.Blue90
import com.dangerfield.notable.designsystem.DarkPurpleGray10
import com.dangerfield.notable.designsystem.DarkPurpleGray90
import com.dangerfield.notable.designsystem.DarkPurpleGray99
import com.dangerfield.notable.designsystem.NotableTypography
import com.dangerfield.notable.designsystem.Orange10
import com.dangerfield.notable.designsystem.Orange20
import com.dangerfield.notable.designsystem.Orange30
import com.dangerfield.notable.designsystem.Orange40
import com.dangerfield.notable.designsystem.Orange80
import com.dangerfield.notable.designsystem.Orange90
import com.dangerfield.notable.designsystem.Purple10
import com.dangerfield.notable.designsystem.Purple20
import com.dangerfield.notable.designsystem.Purple30
import com.dangerfield.notable.designsystem.Purple40
import com.dangerfield.notable.designsystem.Purple80
import com.dangerfield.notable.designsystem.Purple90
import com.dangerfield.notable.designsystem.PurpleGray30
import com.dangerfield.notable.designsystem.PurpleGray50
import com.dangerfield.notable.designsystem.PurpleGray60
import com.dangerfield.notable.designsystem.PurpleGray80
import com.dangerfield.notable.designsystem.PurpleGray90
import com.dangerfield.notable.designsystem.Red10
import com.dangerfield.notable.designsystem.Red20
import com.dangerfield.notable.designsystem.Red30
import com.dangerfield.notable.designsystem.Red40
import com.dangerfield.notable.designsystem.Red80
import com.dangerfield.notable.designsystem.Red90

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    secondary = Orange40,
    onSecondary = Color.White,
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange10,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue90,
    onTertiaryContainer = Blue10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkPurpleGray99,
    onBackground = DarkPurpleGray10,
    surface = DarkPurpleGray99,
    onSurface = DarkPurpleGray10,
    surfaceVariant = PurpleGray90,
    onSurfaceVariant = PurpleGray30,
    outline = PurpleGray50,
)

/**
 * Dark default theme color scheme
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    secondary = Orange80,
    onSecondary = Orange20,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkPurpleGray10,
    onBackground = DarkPurpleGray90,
    surface = DarkPurpleGray10,
    onSurface = DarkPurpleGray90,
    surfaceVariant = PurpleGray30,
    onSurfaceVariant = PurpleGray80,
    outline = PurpleGray60,
)

@Composable
fun NotableTheme(darkTheme: Boolean = true, content: @Composable() () -> Unit) {

    val colorScheme = if (supportsDynamicTheming()) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NotableTypography,
        content = content
    )
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
private fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
