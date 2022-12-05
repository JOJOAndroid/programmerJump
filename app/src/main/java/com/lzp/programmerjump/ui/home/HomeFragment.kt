package com.lzp.programmerjump.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lzp.programmerjump.databinding.FragmentHomeBinding
import com.lzp.programmerjump.entity.Persion
import com.lzp.programmerjump.util.FileUtils
import com.alibaba.fastjson.JSON

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
        homeViewModel?.text?.observe(viewLifecycleOwner) {
        }
        initData()
    }

    fun initData() {
        //Test
        val content: String = FileUtils.parseFiletoString(this.context,"person.json")
        Log.d("LZP","content:$content")
        mPersions = JSON.parseArray(content, Persion::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}