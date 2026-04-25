package com.example.movieapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.movieapplication.ui.activities.LoginActivity
import com.example.movieapplication.R
import com.example.movieapplication.ui.activities.RegisterActivity
import com.example.movieapplication.data.UserPreferences
import com.example.movieapplication.ui.activities.FavoritesActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class ProfileFragment : Fragment() {
    // Logged-in views
    private var layoutLoggedIn: LinearLayout? = null
    private var imgAvatar: ShapeableImageView? = null
    private var txtName: TextView? = null
    private var txtEmail: TextView? = null
    private var btnFavorite: LinearLayout? = null
    private var btnHistory: LinearLayout? = null
    private var btnLogout: LinearLayout? = null

    // Logged-out views
    private var layoutLoggedOut: LinearLayout? = null
    private var btnGoLogin: MaterialButton? = null
    private var btnGoRegister: MaterialButton? = null

    private var userPrefs: UserPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPrefs = UserPreferences.getInstance(requireContext())

        initViews(view)
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật UI mỗi khi quay lại tab (sau login/logout)
        refreshUI()
    }

    // ── Init ──────────────────────────────────────────────────────────────────
    private fun initViews(view: View) {
        layoutLoggedIn = view.findViewById<LinearLayout?>(R.id.layoutLoggedIn)
        imgAvatar = view.findViewById<ShapeableImageView?>(R.id.imgAvatar)
        txtName = view.findViewById<TextView?>(R.id.txtName)
        txtEmail = view.findViewById<TextView?>(R.id.txtEmail)
        btnFavorite = view.findViewById<LinearLayout?>(R.id.btnFavorite)
        btnHistory = view.findViewById<LinearLayout?>(R.id.btnHistory)
        btnLogout = view.findViewById<LinearLayout?>(R.id.btnLogout)

        layoutLoggedOut = view.findViewById<LinearLayout?>(R.id.layoutLoggedOut)
        btnGoLogin = view.findViewById<MaterialButton?>(R.id.btnGoLogin)
        btnGoRegister = view.findViewById<MaterialButton?>(R.id.btnGoRegister)
    }

    private fun setupListeners() {
        // ── Logged-out ──
        btnGoLogin!!.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    getContext(),
                    LoginActivity::class.java
                )
            )
        })

        btnGoRegister!!.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    getContext(),
                    RegisterActivity::class.java
                )
            )
        })

        // ── Logged-in ──
        btnFavorite!!.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    getContext(),
                    FavoritesActivity::class.java
                )
            )
        })

        btnHistory!!.setOnClickListener(View.OnClickListener { v: View? ->
            Toast.makeText(
                getContext(),
                "Lịch sử xem (đang phát triển)",
                Toast.LENGTH_SHORT
            ).show()
        })

        btnLogout!!.setOnClickListener(View.OnClickListener { v: View? ->
            userPrefs?.logout()
            refreshUI()
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        })
    }

    // ── UI state ──────────────────────────────────────────────────────────────
    private fun refreshUI() {
        if (userPrefs?.isLoggedIn() == true) {
            layoutLoggedIn!!.setVisibility(View.VISIBLE)
            layoutLoggedOut!!.setVisibility(View.GONE)

            txtName?.setText(userPrefs?.getUserName())
            txtEmail?.setText(userPrefs?.getUserEmail())
        } else {
            layoutLoggedIn!!.setVisibility(View.GONE)
            layoutLoggedOut!!.setVisibility(View.VISIBLE)
        }
    }
}