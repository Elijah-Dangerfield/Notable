package com.dangerfield.editnote

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

@Composable
fun TextFieldWithHint(
    value: String,
    modifier: Modifier = Modifier,
    hint: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle.Default,
    onDone: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                this.defaultKeyboardAction(ImeAction.Done)
                onDone()
            }),
        )
        if (value.isEmpty()) hint()
    }
}
