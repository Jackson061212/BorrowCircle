package com.example.borrowcircle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.borrowcircle.app.theme.BorrowCircleColors
import com.example.borrowcircle.data.model.ListingDraft
import com.example.borrowcircle.data.model.PricingModel
import com.example.borrowcircle.data.model.RequestDraft
import com.example.borrowcircle.data.model.validationErrors

@Composable
fun CreateChooserDialog(
    onDismiss: () -> Unit,
    onListItem: () -> Unit,
    onPostRequest: () -> Unit,
) {
    AdaptivePanelDialog(title = "Create", onDismiss = onDismiss, dimAmount = 0.12f) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "How would you like to help your circle?",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
            )
            CreateChoiceCard(
                title = "List an item",
                supportingText = "Offer something your circle can borrow.",
                onClick = onListItem,
            )
            CreateChoiceCard(
                title = "Post a request",
                supportingText = "Ask your circle for something you need.",
                onClick = onPostRequest,
            )
        }
    }
}

@Composable
private fun CreateChoiceCard(
    title: String,
    supportingText: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Button
                contentDescription = title
            }
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
fun CreateListingDialog(
    onDismiss: () -> Unit,
    onSubmit: (ListingDraft) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var description by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Good") }
    var accessories by remember { mutableStateOf("Carry case") }
    var pricingModel by remember { mutableStateOf(PricingModel.Free) }
    var price by remember { mutableStateOf("") }
    var minimumLoanDays by remember { mutableStateOf("1") }
    var maximumLoanDays by remember { mutableStateOf("7") }
    var availability by remember { mutableStateOf("Available this week") }
    var latestReturn by remember { mutableStateOf("End of this month") }
    var replacementValue by remember { mutableStateOf("50") }
    var handoffLocation by remember { mutableStateOf("Campus Center lobby") }
    var safetyNotes by remember { mutableStateOf("") }
    var hasSessionPhoto by remember { mutableStateOf(false) }
    var errors by remember { mutableStateOf(emptyMap<String, String>()) }

    CreationDialogFrame(
        title = "List an item",
        subtitle = "Offer something useful to verified NYU Abu Dhabi members.",
        submitLabel = "Publish listing",
        onDismiss = onDismiss,
        onSubmit = {
            val draft = ListingDraft(
                title = title,
                category = category,
                description = description,
                condition = condition,
                accessories = accessories,
                pricingModel = pricingModel,
                price = price,
                minimumLoanDays = minimumLoanDays,
                maximumLoanDays = maximumLoanDays,
                availability = availability,
                latestReturn = latestReturn,
                replacementValue = replacementValue,
                handoffLocation = handoffLocation,
                safetyNotes = safetyNotes,
                hasSessionPhoto = hasSessionPhoto,
            )
            errors = draft.validationErrors()
            if (errors.isEmpty()) onSubmit(draft)
        },
    ) {
        FormField(
            label = "Title",
            value = title,
            onValueChange = { title = it; errors = errors - "title" },
            error = errors["title"],
        )
        FormField(
            label = "Category",
            value = category,
            onValueChange = { category = it; errors = errors - "category" },
            error = errors["category"],
        )
        FormField(
            label = "Description",
            value = description,
            onValueChange = { description = it; errors = errors - "description" },
            error = errors["description"],
            singleLine = false,
            minimumLines = 3,
        )
        PhotoPreviewField(
            selected = hasSessionPhoto,
            error = errors["photo"],
            onSelect = { hasSessionPhoto = true; errors = errors - "photo" },
        )
        FormField(
            label = "Condition",
            value = condition,
            onValueChange = { condition = it },
        )
        FormField(
            label = "Included accessories",
            value = accessories,
            onValueChange = { accessories = it },
            supportingText = "Separate multiple items with commas.",
        )
        ReadOnlyFormValue(label = "Circle visibility", value = "NYU Abu Dhabi")
        PricingSelector(
            selected = pricingModel,
            onSelected = { pricingModel = it; errors = errors - "price" },
        )
        if (pricingModel != PricingModel.Free) {
            FormField(
                label = "Price (AED)",
                value = price,
                onValueChange = { price = it; errors = errors - "price" },
                error = errors["price"],
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField(
                label = "Minimum days",
                value = minimumLoanDays,
                onValueChange = { minimumLoanDays = it; errors = errors - "minimumLoanDays" },
                error = errors["minimumLoanDays"],
                modifier = Modifier.weight(1f),
            )
            FormField(
                label = "Maximum days",
                value = maximumLoanDays,
                onValueChange = { maximumLoanDays = it; errors = errors - "maximumLoanDays" },
                error = errors["maximumLoanDays"],
                modifier = Modifier.weight(1f),
            )
        }
        FormField(
            label = "Availability range",
            value = availability,
            onValueChange = { availability = it; errors = errors - "availability" },
            error = errors["availability"],
        )
        FormField(
            label = "Latest return",
            value = latestReturn,
            onValueChange = { latestReturn = it; errors = errors - "latestReturn" },
            error = errors["latestReturn"],
        )
        FormField(
            label = "Approximate replacement value (AED)",
            value = replacementValue,
            onValueChange = { replacementValue = it; errors = errors - "replacementValue" },
            error = errors["replacementValue"],
            supportingText = "Private in this prototype unless a resolution needs it.",
        )
        FormField(
            label = "Handoff location",
            value = handoffLocation,
            onValueChange = { handoffLocation = it; errors = errors - "handoffLocation" },
            error = errors["handoffLocation"],
            supportingText = "Use a public meeting point, not a home address.",
        )
        FormField(
            label = "Usage or safety notes (optional)",
            value = safetyNotes,
            onValueChange = { safetyNotes = it },
            singleLine = false,
            minimumLines = 2,
        )
    }
}

@Composable
fun CreateRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (RequestDraft) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var description by remember { mutableStateOf("") }
    var neededFrom by remember { mutableStateOf("Tomorrow") }
    var neededUntil by remember { mutableStateOf("This weekend") }
    var maximumBudget by remember { mutableStateOf("") }
    var pickupFlexibility by remember { mutableStateOf("Flexible pickup on campus") }
    var hasReferencePreview by remember { mutableStateOf(false) }
    var errors by remember { mutableStateOf(emptyMap<String, String>()) }

    CreationDialogFrame(
        title = "Post a request",
        subtitle = "Ask your verified circle for something you need.",
        submitLabel = "Post request",
        onDismiss = onDismiss,
        onSubmit = {
            val draft = RequestDraft(
                title = title,
                category = category,
                description = description,
                neededFrom = neededFrom,
                neededUntil = neededUntil,
                circle = "NYU Abu Dhabi",
                maximumBudget = maximumBudget,
                pickupFlexibility = pickupFlexibility,
                hasReferencePreview = hasReferencePreview,
            )
            errors = draft.validationErrors()
            if (errors.isEmpty()) onSubmit(draft)
        },
    ) {
        FormField(
            label = "Title",
            value = title,
            onValueChange = { title = it; errors = errors - "title" },
            error = errors["title"],
        )
        FormField(
            label = "Category",
            value = category,
            onValueChange = { category = it; errors = errors - "category" },
            error = errors["category"],
        )
        FormField(
            label = "Description",
            value = description,
            onValueChange = { description = it; errors = errors - "description" },
            error = errors["description"],
            singleLine = false,
            minimumLines = 3,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField(
                label = "Needed from",
                value = neededFrom,
                onValueChange = { neededFrom = it; errors = errors - "neededFrom" },
                error = errors["neededFrom"],
                modifier = Modifier.weight(1f),
            )
            FormField(
                label = "Needed until",
                value = neededUntil,
                onValueChange = { neededUntil = it; errors = errors - "neededUntil" },
                error = errors["neededUntil"],
                modifier = Modifier.weight(1f),
            )
        }
        ReadOnlyFormValue(label = "Circle", value = "NYU Abu Dhabi")
        FormField(
            label = "Maximum budget in AED (optional)",
            value = maximumBudget,
            onValueChange = { maximumBudget = it; errors = errors - "maximumBudget" },
            error = errors["maximumBudget"],
        )
        FormField(
            label = "Pickup flexibility",
            value = pickupFlexibility,
            onValueChange = { pickupFlexibility = it; errors = errors - "pickupFlexibility" },
            error = errors["pickupFlexibility"],
        )
        OutlinedButton(
            onClick = { hasReferencePreview = !hasReferencePreview },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (hasReferencePreview) "Remove reference preview" else "Add reference preview (optional)")
        }
        if (hasReferencePreview) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Reference preview attached for this session",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun CreationDialogFrame(
    title: String,
    subtitle: String,
    submitLabel: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    fields: @Composable ColumnScope.() -> Unit,
) {
    AdaptivePanelDialog(title = title, onDismiss = onDismiss, dimAmount = 0.12f) {
        Column(
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
            fields()
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(submitLabel, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = "Saved only for this app session.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    supportingText: String? = null,
    singleLine: Boolean = true,
    minimumLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        isError = error != null,
        supportingText = when {
            error != null -> ({ Text(error) })
            supportingText != null -> ({ Text(supportingText) })
            else -> null
        },
        singleLine = singleLine,
        minLines = minimumLines,
        shape = RoundedCornerShape(10.dp),
    )
}

@Composable
private fun PhotoPreviewField(
    selected: Boolean,
    error: String?,
    onSelect: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Product photo",
            fontWeight = FontWeight.SemiBold,
        )
        OutlinedButton(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (selected) "Replace demo photo" else "Add demo photo preview")
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(BorrowCircleColors.Accent, RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✓", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Session photo preview", fontWeight = FontWeight.Medium)
                }
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
            )
        }
        Text(
            text = "Prototype: the preview is not uploaded or persisted.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun ReadOnlyFormValue(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = value, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PricingSelector(
    selected: PricingModel,
    onSelected: (PricingModel) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Pricing", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PricingModel.entries.forEach { model ->
                FilterChip(
                    selected = selected == model,
                    onClick = { onSelected(model) },
                    label = { Text(model.label) },
                )
            }
        }
    }
}
