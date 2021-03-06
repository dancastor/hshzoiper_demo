package com.zoiper.zdk.android.demokt.conference

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zoiper.zdk.Call
import com.zoiper.zdk.Conference
import com.zoiper.zdk.EventHandlers.ConferenceEventsHandler
import com.zoiper.zdk.Types.CallLineStatus
import com.zoiper.zdk.android.demokt.R
import com.zoiper.zdk.android.demokt.util.TextViewSelectionUtils
import kotlinx.android.synthetic.main.call_item.view.*
import java.util.ArrayList

/**
 * CallAdapter
 *
 * @since 4.2.2019 г.
 */
class CallAdapter : RecyclerView.Adapter<CallAdapter.CallViewHolder>(), ConferenceEventsHandler {

    private var callList = mutableListOf<Call>()
        set(new) {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean{
                    return new.getOrNull(newItemPos) == field.getOrNull(oldItemPos)
                }
                override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean{
                    return new.getOrNull(newItemPos) == field.getOrNull(oldItemPos)
                }
                override fun getOldListSize() = field.size
                override fun getNewListSize() = new.size
            }).dispatchUpdatesTo(this)

            field = new
        }

    override fun onConferenceParticipantRemoved(conf: Conference?, call: Call?) { conf?.calls()?.let { callList = it } }
    override fun onConferenceParticipantJoined(conf: Conference?, call: Call?) { conf?.calls()?.let { callList = it } }

//    init {
//        this.callList = ArrayList()
//    }
//
//    fun addNewCall(newCall: Call) {
//        callList.add(newCall)
//        val indexOfNewItem = callList.indexOf(newCall)
//        notifyItemInserted(indexOfNewItem)
//    }
//
//    fun removeCall(call: Call) {
//        val iterator = callList.listIterator()
//
//        while (iterator.hasNext()) {
//            val currentCall = iterator.next()
//            if (currentCall.callHandle() == call.callHandle()) {
//                val position = callList.indexOf(currentCall)
//                callList.remove(currentCall)
//                notifyItemRemoved(position)
//                // Remove the call from the conference.
//                conference.removeCall(call, true)
//                // Hangup the call. This is not necessary.
//                if (call.status().lineStatus() != CallLineStatus.Terminated) {
//                    call.hangUp()
//                }
//            }
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.call_item,
            parent,
            false
        )
        return CallViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CallViewHolder, position: Int) = viewHolder.bind(callList[position])
    override fun onBindViewHolder(viewHolder: CallViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(viewHolder, position)
        } else {
            // Perform a partial update
            for (payload in payloads) {
                if (payload is CallLineStatus) {
                    viewHolder.bindCallStatus(payload)
                }
            }
        }
    }

    override fun getItemCount(): Int = callList.size

//    private fun muteCall(call: Call, view: View) {
//        val newSelectedState = !view.isSelected
//        if (newSelectedState) {
//            conference.muteCall(call)
//            call.muted(true)
//        } else {
//            conference.unmuteCall(call)
//            call.muted(false)
//        }
//        TextViewSelectionUtils.setTextViewSelected(view as TextView, newSelectedState)
//    }

//    fun setCallStatus(call: Call) {
//        for (currentCall in callList) {
//            if (currentCall.callHandle() == call.callHandle()) {
//                val index = callList.indexOf(currentCall)
//                notifyItemChanged(index, call.status().lineStatus())
//            }
//        }
//    }
//
//    fun containsCall(call: Call?): Boolean {
//        for (currentCall in callList) {
//            if (call != null && currentCall.callHandle() == call.callHandle()) {
//                return true
//            }
//        }
//        return false
//    }

    inner class CallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        internal fun bind(call: Call) {
            itemView.callItemTvName.text = "${call.calleeName()} (${call.calleeNumber()})"
            itemView.callItemTvStatus.text = call.status().lineStatus().toString()
            itemView.callItemTvCount.text = "$itemCount"

//            itemView.callItemTvMute.setOnClickListener { muteCall(call, it) }
//            itemView.callItemTvRemove.setOnClickListener { removeCall(call) }
        }

        /**
         * Update the call status.
         *
         * @param payload
         * The call line status object.
         */
        fun bindCallStatus(payload: CallLineStatus) {
            itemView.callItemTvStatus.text = payload.toString()
        }
    }
}
