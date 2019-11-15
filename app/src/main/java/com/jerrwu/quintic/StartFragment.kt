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


class StartFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var cardList: ArrayList<Card> = ArrayList()

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        setInfoCardGreeting(prefs)
        setInfoCardName(prefs)
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

        var card1 = Card()
        card1.id = 0
        card1.title = "Image card"
        card1.content = "hie..."
        card1.ic = R.drawable.image_placeholder
        cardList.add(card1)

        var card2 = Card()
        card2.id = 1
        card2.title = "Non-image card"
        card2.content = "a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string a really long test string "
        card2.ic = 0
        cardList.add(card2)

        for (i in 2..6) {
            val card = Card()
            card.id = i
            card.ic = R.drawable.dots_horizontal_circle_outline
            card.title = "Title $i"
            card.content = "this is text this is text this is text this is text this is text this is text this is text this is text this is text this is text this is text this is text "
            cardList.add(card)
        }

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
            val curHour: String =  current.format(formatter)
            greetingString = InfoHelper.getGreeting(curHour)
            infoCardGreeting.visibility = View.VISIBLE
        } else {
            greetingString = ""
            infoCardGreeting.visibility = View.GONE
        }
        infoCardGreeting.text = greetingString.replace("!",",")
    }

    private fun setInfoCardName(prefs: SharedPreferences) {
        val nameString: String? = prefs.getString("name", "user")
        infoCardName.text = nameString
    }

}
