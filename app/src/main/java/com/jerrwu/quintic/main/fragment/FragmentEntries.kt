package com.jerrwu.quintic.main.fragment


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jerrwu.quintic.*
import com.jerrwu.quintic.account.AccountActivity
import com.jerrwu.quintic.entities.card.CardEntity
import com.jerrwu.quintic.entities.card.adapter.CardAdapter
import com.jerrwu.quintic.entry.EntryActivity
import com.jerrwu.quintic.helpers.DbHelper
import com.jerrwu.quintic.helpers.InfoHelper
import kotlinx.android.synthetic.main.fragment_entries.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FragmentEntries : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var cardList: ArrayList<CardEntity> = ArrayList()

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)
        infoCardNameRem(prefs)

        loadQuery("%")
        mAdapter?.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_entries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)
        infoCardNameRem(prefs)

        nameRemButton.setOnClickListener {
            val intent: Intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
        }

        nameRemDismissButton.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean("setNameRem", false)
            editor.apply()
            infoCardNameRem(prefs)
        }

        loadQuery("%")

        mRecyclerView = activity!!.findViewById(R.id.recycler_view)
        var mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = CardAdapter(cardList)
        mRecyclerView!!.adapter = mAdapter
        (mAdapter as CardAdapter).onItemClick = { card ->
            // Toast.makeText(activity, card.id.toString(), Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, EntryActivity::class.java)
            intent.putExtra("ID", card.id)
            intent.putExtra("Title", card.title)
            intent.putExtra("Content", card.content)
            intent.putExtra("Time", card.time.toString())
            startActivity(intent)
        }
    }

    private fun setInfoCardGreeting(prefs: SharedPreferences) {
        val greetingString: String?
        val greetingsToggle = prefs.getBoolean("greetings", true)
        if (greetingsToggle) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH")
            val curHour: String = current.format(formatter)
            greetingString = InfoHelper.getGreeting(curHour)
            fragmentEntriesInfoCard.visibility = View.VISIBLE
        } else {
            greetingString = ""
            fragmentEntriesInfoCard.visibility = View.GONE
        }
        infoCardGreeting.text = greetingString.replace("!", ",")
    }

    private fun setInfoCardName(prefs: SharedPreferences) {
        val nameString: String? = prefs.getString("name", "user")
        infoCardName.text = nameString
    }

    private fun infoCardNameRem(prefs: SharedPreferences) {
        val remBool: Boolean = prefs.getBoolean("setNameRem", false)
        if (remBool) {
            infoCardGreetingType.visibility = View.GONE
            infoCardRemType.visibility = View.VISIBLE
            infoCardGreetingIcon.visibility = View.GONE
            infoCardReminderIcon.visibility = View.VISIBLE
        }
        else {
            infoCardGreetingType.visibility = View.VISIBLE
            infoCardRemType.visibility = View.GONE
            infoCardGreetingIcon.visibility = View.VISIBLE
            infoCardReminderIcon.visibility = View.GONE
        }
    }

    private fun loadQuery(title: String) {
        val dbManager = DbHelper(activity!!)
        val projections = arrayOf("ID", "Image", "Title", "Content", "DateTime")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.query(
            projections, "Title like ?", selectionArgs, "ID"+" DESC")
        cardList.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val ic = cursor.getInt(cursor.getColumnIndex("Image"))
                val cdTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val cdCont = cursor.getString(cursor.getColumnIndex("Content"))
                val cdTime = cursor.getString(cursor.getColumnIndex("DateTime"))

                cardList.add(
                    CardEntity(
                        id,
                        ic,
                        cdTitle,
                        cdCont,
                        LocalDateTime.parse(cdTime)
                    )
                )

            } while (cursor.moveToNext())
        }
    }

}
