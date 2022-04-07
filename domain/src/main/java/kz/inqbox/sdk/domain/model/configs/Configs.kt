package kz.inqbox.sdk.domain.model.configs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.inqbox.sdk.domain.model.call.type.CallType
import kz.inqbox.sdk.domain.model.i18n.I18NId
import kz.inqbox.sdk.domain.model.i18n.I18NString

@Parcelize
data class Configs constructor(
    val bot: Bot,
    val callAgent: CallAgent,
    val preferences: Preferences,

    val contacts: Contacts? = null,

    val calls: List<Call>? = null,
    val services: List<Service>? = null,
    val forms: List<Form>? = null,
    val websites: List<Website>? = null
) : Parcelable {

    @Parcelize
    data class Bot constructor(
        val image: String? = null,
        val title: String? = null
    ) : Parcelable

    @Parcelize
    data class CallAgent constructor(
        val defaultName: String? = null
    ) : Parcelable

    @Parcelize
    data class Preferences constructor(
        val isChatBotEnabled: Boolean = false,

        val isPhonesListShown: Boolean = false,
        val isContactSectionsShown: Boolean = false,

        val isAudioCallEnabled: Boolean = false,
        val isVideoCallEnabled: Boolean = false,

        val isServicesEnabled: Boolean = false,

        val isCallAgentsScoped: Boolean = false
    ) : Parcelable

    @Parcelize
    open class Nestable internal constructor(
        open val id: Long,
        open val parentId: Long,
        open val type: Type?,
        open val title: I18NString,
        open val extra: Extra? = null
    ) : Parcelable {

        companion object {
            const val NO_PARENT_ID: Long = 0L
        }

        @Parcelize
        data class Extra constructor(
            val order: Int? = null,
            val behavior: Behavior? = null
        ) : Parcelable {

            @Parcelize
            enum class Behavior : Parcelable {
                UNKNOWN,
                REGULAR,
                REQUEST_LOCATION
            }

        }

        @Parcelize
        enum class Type : Parcelable {
            LINK,
            FOLDER
        }

        fun isParent(): Boolean = parentId == NO_PARENT_ID

        fun isFolder(): Boolean = type == Type.FOLDER

        fun isLink(): Boolean = type == Type.LINK

    }

    @Parcelize
    data class Call constructor(
        override val id: Long,
        override val parentId: Long,
        override val type: Type?,
        val callType: CallType? = null,
        val scope: String? = null,
        override val title: I18NString,
        override val extra: Extra? = null
    ) : Nestable(id = id, parentId = parentId, type = type, title = title, extra = extra),
        Parcelable {

        fun isAudioCall(): Boolean = callType == CallType.AUDIO

        fun isVideoCall(): Boolean = callType == CallType.VIDEO

        fun isMediaCall(): Boolean = isAudioCall() || isVideoCall()

    }

    @Parcelize
    data class Service constructor(
        override val id: Long,
        override val parentId: Long,
        override val type: Type?,
        val serviceId: Long,
        override val title: I18NString,
        override val extra: Extra? = null
    ) : Nestable(id = id, parentId = parentId, type = type, title = title, extra = extra),
        Parcelable

    @Parcelize
    data class Form constructor(
        override val id: Long,
        override val parentId: Long,
        override val type: Type?,
        val formId: I18NId,
        override val title: I18NString,
        override val extra: Extra? = null
    ) : Nestable(id = id, parentId = parentId, type = type, title = title, extra = extra),
        Parcelable

    @Parcelize
    data class Website constructor(
        override val id: Long,
        override val parentId: Long,
        override val type: Type?,
        val url: String,
        override val title: I18NString,
        override val extra: Extra? = null
    ) : Nestable(id = id, parentId = parentId, type = type, title = title, extra = extra),
        Parcelable

    @Parcelize
    data class Contacts constructor(
        val phoneNumbers: List<PhoneNumber>? = null,
        val socials: List<Social>? = null
    ) : Parcelable {

        @Parcelize
        data class Social constructor(val id: Id, val url: String) : Parcelable {

            @Parcelize
            enum class Id constructor(val id: String) : Parcelable {
                FACEBOOK("fb"),
                TELEGRAM("tg"),
                TWITTER("tw"),
                VK("vk"),
                WHATSAPP("whatsapp")
            }

        }

        @Parcelize
        data class PhoneNumber constructor(
            val value: String,
            val info: I18NString? = null,
            val action: String? = null
        ) : Parcelable

    }

}