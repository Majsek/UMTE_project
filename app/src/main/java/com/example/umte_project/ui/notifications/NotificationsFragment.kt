package com.example.umte_project.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umte_project.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(requireContext())

        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            if (notifications.isEmpty()) {
                binding.textNotifications.visibility = View.VISIBLE
                binding.recyclerViewNotifications.visibility = View.GONE
            } else {
                binding.textNotifications.visibility = View.GONE
                binding.recyclerViewNotifications.visibility = View.VISIBLE
                adapter = NotificationAdapter(notifications)
                binding.recyclerViewNotifications.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
