package com.jerrwu.quintic


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
import kotlinx.android.synthetic.main.fragment_start.*
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)

        loadQuery("%")

//        var card1 = Card(0, R.drawable.image_placeholder, "Image card", "hie...")
//        cardList.add(card1)
//
//        var card2 = Card(
//            1,
//            0,
//            "Non-image card",
//            "a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string "
//        )
//        cardList.add(card2)
//
//        for (i in 2..6) {
//            val card = Card(
//                i,
//                R.drawable.dots_horizontal_circle_outline,
//                "Title $i",
//                "this is text this is text this is text this is text this is text this is text this is text this is text this is text this is text this is text this is text "
//            )
//            cardList.add(card)
//        }

        mRecyclerView = activity!!.findViewById(R.id.recycler_view)
        var mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = CardAdapter(cardList)
        mRecyclerView!!.adapter = mAdapter
        (mAdapter as CardAdapter).onItemClick = { card ->
            Toast.makeText(activity, card.id.toString(), Toast.LENGTH_SHORT).show()
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
            infoCardGreeting.visibility = View.VISIBLE
        } else {
            greetingString = ""
            infoCardGreeting.visibility = View.GONE
        }
        infoCardGreeting.text = greetingString.replace("!", ",")
    }

    private fun setInfoCardName(prefs: SharedPreferences) {
        val nameString: String? = prefs.getString("name", "user")
        infoCardName.text = nameString
    }

    private fun loadQuery(title: String) {
        val dbManager = DbManager(activity!!)
        val projections = arrayOf("ID", "Image", "Title", "Content")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        cardList.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val ic = cursor.getInt(cursor.getColumnIndex("Image"))
                val cdTitle = cursor.getString(cursor.getColumnIndex("Title"))
                val cdCont = cursor.getString(cursor.getColumnIndex("Content"))

                cardList.add(Card(id, ic, cdTitle, cdCont))

            } while (cursor.moveToNext())
        }
    }

}
