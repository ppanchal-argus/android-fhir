/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.extensions

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.card.MaterialCardView
import org.hl7.fhir.r4.model.Questionnaire

/** Displays `localizedText` if it is not null or empty, or hides the [TextView]. */
fun TextView.updateTextAndVisibility(localizedText: Spanned? = null) {
  text = localizedText
  visibility =
    if (localizedText.isNullOrEmpty()) {
      GONE
    } else {
      VISIBLE
    }
}

/** Returns [VISIBLE] if any of the [view] is visible, [GONE] otherwise. */
fun getHeaderViewVisibility(vararg view: TextView): Int {
  if (view.any { it.visibility == VISIBLE }) {
    return VISIBLE
  }
  return GONE
}

/**
 * Initializes the text for [helpTextView] with instructions on how to use the feature, and sets the
 * visibility and click listener for the [helpButton] to allow users to access the help information
 * and toggles the visibility for view [helpCardView].
 */
fun initHelpViews(
  helpButton: Button,
  helpCardView: MaterialCardView,
  helpTextView: TextView,
  helpTextViewMore: TextView,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
) {
  helpCardView.visibility = GONE
  helpButton.visibility =
    if (questionnaireItem.hasHelpButton) {
      VISIBLE
    } else {
      GONE
    }
  helpTextView.updateTextAndVisibility(questionnaireItem.localizedHelpSpanned)
  var isHelpTextLineCountSet = false
  helpButton.setOnClickListener {
    helpCardView.visibility =
      when (helpCardView.visibility) {
        VISIBLE -> GONE
        else -> VISIBLE
      }
    if (helpCardView.visibility == VISIBLE && !isHelpTextLineCountSet) {
      helpTextView.viewTreeObserver.addOnPreDrawListener(
        object : ViewTreeObserver.OnPreDrawListener {
          override fun onPreDraw(): Boolean {
            helpTextView.viewTreeObserver.removeOnPreDrawListener(this)
            if (helpTextView.lineCount > helpTextView.maxLines) {
              helpTextViewMore.visibility = VISIBLE
              helpTextViewMore.text =
                helpTextViewMore.context.applicationContext.getString(R.string.text_view_more)
            }
            return true
          }
        },
      )
      // Set a click listener on the TextView and the "View More" text view
      helpTextView.setOnClickListener { toggle(helpTextView, helpTextViewMore) }
      helpTextViewMore.setOnClickListener { toggle(helpTextView, helpTextViewMore) }
      isHelpTextLineCountSet = true
    }
  }
}

private fun toggle(
  textViewContent: TextView,
  textViewViewMore: TextView,
) {
  if (textViewContent.maxLines == 2) {
    textViewViewMore.text =
      textViewContent.context.applicationContext.getString(R.string.text_view_less)
    textViewContent.maxLines = Integer.MAX_VALUE
  } else {
    textViewContent.maxLines = 2
    textViewViewMore.text =
      textViewContent.context.applicationContext.getString(R.string.text_view_more)
  }
}

/**
 * Appends ' *' to [Questionnaire.QuestionnaireItemComponent.localizedTextSpanned] text if
 * [Questionnaire.QuestionnaireItemComponent.required] is true.
 */
fun appendAsteriskToQuestionText(
  context: Context,
  questionnaireViewItem: QuestionnaireViewItem,
): Spanned {
  return SpannableStringBuilder().apply {
    questionnaireViewItem.questionText?.let { append(it) }
    if (
      questionnaireViewItem.questionViewTextConfiguration.showAsterisk &&
        questionnaireViewItem.questionnaireItem.required &&
        !questionnaireViewItem.questionnaireItem.localizedTextSpanned.isNullOrEmpty()
    ) {
      append(context.applicationContext.getString(R.string.space_asterisk))
    }
  }
}
