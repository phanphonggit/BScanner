package com.ntf.bscanner

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.ntf.bscanner.databinding.FragmentFirstBinding
import com.ntf.bscanner.databinding.FragmentScannerBinding
import java.util.*
import java.util.jar.Manifest

class ScannerFragment : Fragment() {

    //private val navigationArgs : ScannerFragmentArgs as navArgs()
    private var _binding: FragmentScannerBinding?= null
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner

    private var _scanResult: String = ""
    val scanResult get() = _scanResult

    val CAMERA_PERMISSION = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        //var btnKeepScanning = view.findViewById<Button>(R.id.btn_keep_scanning)
        //var btnGetResult = view.findViewById<Button>(R.id.btn_get_result)
        val activity = requireActivity()

        binding.btnKeepScanning.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.btnGetResult.setOnClickListener {
            codeScanner.releaseResources()
            finishScanner()
        }

        codeScanner = CodeScanner(activity, binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                Toast.makeText(requireContext(), it.text, Toast.LENGTH_LONG).show()
                _scanResult = it.text
            }
        }
        checkPermission()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    private fun finishScanner(){
        val action = ScannerFragmentDirections.actionScannerFragmentToFirstFragment()
        this.findNavController().navigate(action)
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION)
        } else {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            codeScanner.startPreview()
        } else{
            Toast.makeText(requireContext(), "Can not Scan until have CAMERA permission!", Toast.LENGTH_LONG).show()
        }
    }
}