package com.jerrwu.quintic


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_entries.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FragmentEntries : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var cardList: ArrayList<Card> = ArrayList()

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)

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

        loadQuery("%")

        mRecyclerView = activity!!.findViewById(R.id.recycler_view)
        var mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = CardAdapter(cardList)
        mRecyclerView!!.adapter = mAdapter
        (mAdapter as CardAdapter).onItemClick = { card ->
            Toast.makeText(activity, card.id.toString(), Toast.LENGTH_SHORT).show()
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

    private fun loadQuery(title: String) {
        val dbManager = DbManager(activity!!)
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

                cardList.add(Card(id, ic, cdTitle, cdCont, LocalDateTime.parse(cdTime)))

            } while (cursor.moveToNext())
        }
    }

}
