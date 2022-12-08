package com.lzp.programmerjump.ui.home

import android.app.ActivityManager
import android.app.ActivityManager.RECENT_WITH_EXCLUDED
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alibaba.fastjson.JSON
import com.lzp.programmerjump.R
import com.lzp.programmerjump.databinding.FragmentHomeBinding
import com.lzp.programmerjump.entity.Persion
import com.lzp.programmerjump.util.FileUtils
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    /**
     * 车控数据
     */
    private var mPersions: List<Persion>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var homeViewModel: HomeViewModel ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initObserve()

        return root
    }

    fun initObserve() {
        initData()
    }

    fun initData() {
        //Test
        val content: String = FileUtils.parseFiletoString(this.context,"person.json")
        Log.d("LZP","content:$content")
        mPersions = JSON.parseArray(content, Persion::class.java)
        mPersions?.let {
            for(bean in it) {
                binding.name.text = String.format(context!!.resources!!.getString(R.string.name),bean.name)
                binding.readtime.text = String.format(context!!.resources!!.getString(R.string.readtime),bean.readtime)
                binding.sporttime.text = String.format(context!!.resources!!.getString(R.string.sporttime),bean.sporttime)
                binding.learntime.text = String.format(context!!.resources!!.getString(R.string.learntime),bean.learntime)
                binding.totletime.text = String.format(context!!.resources!!.getString(R.string.totletime),bean.totletime)
                binding.expendtime.text = String.format(context!!.resources!!.getString(R.string.expendtime),bean.expendtime)
                binding.treasure.text = String.format(context!!.resources!!.getString(R.string.treasure),bean.treasure)
                binding.phpConsumpotion.text = String.format(context!!.resources!!.getString(R.string.php_consumpotion),bean.php_consumpotion)
            }
        }
        binding.add.setOnClickListener {

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}