package com.wafflestudio.siksha2.components

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ItemCalendarSelectViewBinding
import com.wafflestudio.siksha2.databinding.ItemWeekBinding
import com.wafflestudio.siksha2.utils.getInflater
import com.wafflestudio.siksha2.utils.visibleOrGone
import timber.log.Timber
import java.time.LocalDate
import kotlin.math.abs

class CalendarSelectView : LinearLayout {

    private val binding = ItemCalendarSelectViewBinding.inflate(LayoutInflater.from(context), this)
    private var year = 2021
    private var month = 1
    private var today = LocalDate.now()
    private lateinit var focusingDate: LocalDate
    private lateinit var calendarAdapter: CalendarWeekAdapter
    private lateinit var calendarLayoutManager: LinearLayoutManager
    private lateinit var gestureDetector: GestureDetector
    private var dateChangeListener: OnDateChangeListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        init()
    }

    private fun init() {
        gestureDetector = GestureDetector(
            context,
            object : GestureDetector.OnGestureListener {
                override fun onDown(p0: MotionEvent): Boolean { return false }
                override fun onShowPress(p0: MotionEvent) {}
                override fun onSingleTapUp(p0: MotionEvent): Boolean { return false }
                override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
                    Timber.d("scroll velocity x : $p2 y : $p3")
                    if (abs(p2) > 100) return true
                    return false
                }
                override fun onLongPress(p0: MotionEvent) {}
                override fun onFling(
                    p0: MotionEvent,
                    p1: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    Timber.d(velocityX.toString())

                    if (Math.abs(velocityY) > Math.abs(velocityX)) return false

                    if (velocityX > 250) {
                        val localDate = LocalDate.of(year, month, 1)
                        setDate(localDate.minusMonths(1))
                        return true
                    }

                    if (velocityX < -250) {
                        val localDate = LocalDate.of(year, month, 1)
                        setDate(localDate.plusMonths(1))
                        return true
                    }

                    return false
                }
            }
        )

        binding.leftArrow.setOnClickListener {
            val localDate = LocalDate.of(year, month, 1)
            setDate(localDate.minusMonths(1))
        }

        binding.rightArrow.setOnClickListener {
            val localDate = LocalDate.of(year, month, 1)
            setDate(localDate.plusMonths(1))
        }

        binding.weekRecyclerview.setOnTouchListener { _, ev ->
            gestureDetector.onTouchEvent(ev)
            false
        }

        calendarAdapter = CalendarWeekAdapter()
        calendarLayoutManager = LinearLayoutManager(context)
        binding.weekRecyclerview.apply {
            adapter = calendarAdapter
            layoutManager = calendarLayoutManager
        }
    }

    fun updateDate(date: LocalDate) {
        setDate(date)
        focusingDate = date
        dateChangeListener?.onChange(focusingDate)
    }

    fun updateDateWithoutListener(date: LocalDate) {
        setDate(date)
        focusingDate = date
    }

    fun setDateChangeListener(listener: OnDateChangeListener) {
        dateChangeListener = listener
    }

    fun setSelectedDate(date: LocalDate) {
        setDate(date)
        focusingDate = date
    }

    private fun setDate(date: LocalDate) {
        year = date.year
        month = date.monthValue
        val yearMonthText = year.toString() + "." +
            (if (month > 9) month.toString() else "0$month")
        binding.yearMonthText.text = yearMonthText

        val weeks = mutableListOf<List<Int>>()

        var startDate = LocalDate.of(year, month, 1)
        val firstWeek = mutableListOf<Int>()
        val startDayOfWeek = startDate.dayOfWeek.value % 7
        for (i in 0 until startDayOfWeek) firstWeek.add(0)
        for (i in 0 until 7 - startDayOfWeek) firstWeek.add(i + 1)
        weeks.add(firstWeek)
        startDate = startDate.plusDays((7 - startDayOfWeek).toLong())
        while (startDate.monthValue == month) {
            val week = mutableListOf<Int>()
            for (i in 0 until 7) {
                if (startDate.monthValue != month) {
                    week.add(0)
                } else {
                    week.add(startDate.dayOfMonth)
                    startDate = startDate.plusDays(1)
                }
            }
            weeks.add(week)
        }

        calendarAdapter.weeks = weeks
        calendarAdapter.notifyDataSetChanged()
    }

    private inner class CalendarWeekAdapter : RecyclerView.Adapter<CalendarWeekViewHolder>() {

        var weeks = listOf<List<Int>>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarWeekViewHolder {
            val binding = ItemWeekBinding.inflate(parent.getInflater(), parent, false)
            return CalendarWeekViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CalendarWeekViewHolder, position: Int) {
            holder.render(weeks[position])
        }

        override fun getItemCount() = weeks.size
    }

    private inner class CalendarWeekViewHolder(binding: ItemWeekBinding) : RecyclerView.ViewHolder(binding.root) {
        private val days = listOf(
            binding.day1,
            binding.day2,
            binding.day3,
            binding.day4,
            binding.day5,
            binding.day6,
            binding.day7
        )

        fun render(weekData: List<Int>) {
            weekData.forEachIndexed { index, data ->
                if (data == 0) {
                    days[index].dayText.visibility = View.INVISIBLE
                } else {
                    days[index].dayText.apply {
                        visibleOrGone(true)
                        text = data.toString()
                        if (focusingDate.year == year &&
                            focusingDate.monthValue == month &&
                            focusingDate.dayOfMonth == data
                        ) {
                            setBackgroundResource(R.drawable.frame_day_select)
                            setTextColor(ContextCompat.getColor(context, R.color.white))
                        } else if (today.year == year &&
                            today.monthValue == month &&
                            today.dayOfMonth == data
                        ) {
                            setBackgroundResource(R.drawable.frame_day_today)
                            setTextColor(ContextCompat.getColor(context, R.color.gray_700))
                        } else {
                            setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                            setTextColor(ContextCompat.getColor(context, R.color.gray_700))
                        }
                        setOnClickListener {
                            updateDate(LocalDate.of(year, month, data))
                        }
                    }
                }
            }
        }
    }

    interface OnDateChangeListener {
        fun onChange(date: LocalDate)
    }
}
