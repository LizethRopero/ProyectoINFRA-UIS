package com.proyecto.infrauis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ReportAdapter(private val context: Context, private val reports: List<Report>) : BaseAdapter() {

    override fun getCount(): Int = reports.size

    override fun getItem(position: Int): Any = reports[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.report_list_item, parent, false)
        val report = reports[position]

        val reportIcon: ImageView = view.findViewById(R.id.reportIcon)
        val reportName: TextView = view.findViewById(R.id.reportName)
        val reportLocation: TextView = view.findViewById(R.id.reportLocation)
        val reportStatus: TextView = view.findViewById(R.id.reportStatus)

        if (report.imageUrl != null && report.imageUrl.isNotEmpty()) {
            Glide.with(context).load(report.imageUrl).into(reportIcon)
        } else {
            reportIcon.setImageResource(R.drawable.ic_report_paper) // Placeholder if no image URL
        }

        reportName.text = report.name
        reportLocation.text = report.location
        reportStatus.text = "Estado: ${report.status}"

        return view
    }
}
