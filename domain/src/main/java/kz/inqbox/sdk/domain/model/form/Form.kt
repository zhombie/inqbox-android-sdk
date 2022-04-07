package kz.inqbox.sdk.domain.model.form

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.garage.chat.model.reply_markup.ReplyMarkup
import kz.garage.file.extension.Extension

@Parcelize
data class Form constructor(
    val id: Long,
    val title: String,
    val prompt: String? = null,
    val isFlexible: Boolean = false,
    val fields: List<Field>,
    val configs: Configs? = null
) : Parcelable {

    @Parcelize
    data class Field constructor(
        val id: Long,
        val isFlexible: Boolean = false,
        val title: String,
        val prompt: String? = null,
        val type: Type,
        val defaultValue: String? = null,
        val info: Info? = null,
        val configs: Configs? = null,
        val level: Int = 0,
        val replyMarkup: ReplyMarkup? = null,
        val options: List<Option>? = null,  // Multiple-selection options
        val isRequired: Boolean = false,
        val conditions: Conditions? = null,
        val autofill: Autofill? = null,
        val value: String? = null  // Value of the field
    ) : Parcelable {

        @Parcelize
        enum class Type constructor(val key: String) : Parcelable {
            TEXT("text"),
            IMAGE("image"),
            AUDIO("audio"),
            VIDEO("video"),
            DOCUMENT("document"),
            FILE("file"),
            SELECT("select"),
            BOOLEAN("boolean"),
            PHONE_NUMBER("phone_number")
        }

        @Parcelize
        data class Configs constructor(
            // Multiple-choice or local media selection
            val isMultipleSelection: Boolean? = null,
            val maxSelectionCount: Int? = null,

            // Select identifier
            val key: String? = null,

            // Input text validation
            val regexp: String? = null,
            val regexpExplanation: String? = null,

            // Input text constraints
            val inputTextMaxLength: Int? = null,
            val inputTextMaxLines: Int? = null
        ) : Parcelable

        @Parcelize
        data class Info constructor(
            val extension: Extension? = null,
            val width: Int? = null,
            val height: Int? = null,
            val duration: Long? = null,  // milliseconds
            val dateAdded: Long? = null,  // milliseconds
            val dateModified: Long? = null,  // milliseconds
            val dateTaken: Long? = null,  // milliseconds
            val size: Long? = null
        ) : Parcelable

        @Parcelize
        data class Option constructor(
            val id: Long,
            val title: String,
            val parentId: Long? = null,
            val key: String,
            val value: String? = null
        ) : Parcelable

        @Parcelize
        data class Conditions constructor(
            val keyboard: List<Condition>? = null,
        ) : Parcelable {

            @Parcelize
            data class Condition constructor(
                val payload: String? = null,
                val nextStep: Int? = null,
                val showSteps: List<Int>? = null,
                val hideSteps: List<Int>? = null
            ) : Parcelable

        }

        @Parcelize
        data class Autofill constructor(
            val qualifier: Qualifier? = null
        ) : Parcelable {

            @Parcelize
            enum class Qualifier : Parcelable {
                UNKNOWN,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                USER_PATRONYMIC,
                USER_FULL_NAME,
                USER_IIN,
                USER_EMAIL,
                USER_PHONE_NUMBER,
                USER_GEOLOCATION
            }
        }
    }

    @Parcelize
    data class Configs constructor(
        val assignees: List<Long>? = null,
        val projectId: Long = NO_PROJECT_ID
    ) : Parcelable {

        companion object {
            const val NO_PROJECT_ID = -1L
        }

    }

}