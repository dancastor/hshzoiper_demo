package com.zoiper.zdk.android.demokt.conference

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.zoiper.zdk.Call
import com.zoiper.zdk.EventHandlers.CallEventsHandler
import com.zoiper.zdk.android.demokt.INTENT_EXTRA_ACCOUNT_ID
import com.zoiper.zdk.android.demokt.R
import com.zoiper.zdk.android.demokt.base.BaseActivity
import com.zoiper.zdk.android.demokt.util.textPromptDialog
import kotlinx.android.synthetic.main.activity_conference.*

/**
 *ConferenceActivity
 *
 *@since 29/03/2019
 */
class ConferenceActivity : BaseActivity(), CallEventsHandler {

    private val account by lazy {
        val longExtra = intent.getLongExtra(INTENT_EXTRA_ACCOUNT_ID, -1)
        getAccount(longExtra)
    }

    private val conferenceAdapter by lazy {
        ConferenceAdapter(
            zdkContext.conferenceProvider(),
            this@ConferenceActivity::promptCreateCall
        )
    }

    private fun promptCreateCall(callback: (Call) -> Unit) {
        textPromptDialog(
            this,
            inputHint = "Enter number",
            message = "Add new call: ",
            positive = "Add"
        ) { number ->
            account
                ?.createCall(number, false, false)
                ?.also(callback)
            //                        ?.let(this::addConference)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)
        setSupportActionBar(conferenceToolbar)

        supportActionBar?.setTitle(R.string.conference)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        conferenceRvConferences.adapter = conferenceAdapter

        conferenceRvConferences.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        )
    }

    override fun onZoiperLoaded() {
        conferenceFabAdd.setOnClickListener { promptAddConference() }

        zdkContext
            .conferenceProvider()
            .addConferenceProviderListener(conferenceAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()

        zdkContext
            .conferenceProvider()
            .dropConferenceProviderListener(conferenceAdapter)
    }

    private fun promptAddConference() {
        textPromptDialog(
            this,
            inputHint = "Enter number",
            message = "Add new call: ",
            positive = "Add"
        ) { number ->
            account
                ?.createCall(number, false, false)
                ?.let { mutableListOf(it) }
                ?.also { zdkContext.conferenceProvider().createConference(it) }
        }
    }
}