package com.yhy.eventjudgement;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.BasicStroke;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;

/**
 * This class is designed to draw spline chart.
 * @author Haiyou Yu
 *
 */

public class WaveChart {

	
	private static XYSeriesCollection dataset = null;
	private static JFreeChart xylineChart = null;

	private WaveChart() {

	}

	/**
	 * 
	 * @param vd  , data point list,such as:(0,50),(1,85.6)......
	 * @param display , if true ,indicates show the chart.
	 * @return the instance of JFreeChart
	 */
	public static JFreeChart init(ArrayList<Double> vd, boolean display) {

		XYSeries coordinates = new XYSeries("coordinates");
		for (int i = 0; i < vd.size(); i++) {
			coordinates.add(i, vd.get(i));
		}
		dataset = new XYSeriesCollection();
		dataset.addSeries(coordinates);

		xylineChart = ChartFactory.createXYLineChart("wave chart", "X", "Y", dataset, PlotOrientation.VERTICAL, true,
				true, false);
		xylineChart.setBackgroundPaint(Color.WHITE);
		xylineChart.setBorderVisible(true);
		xylineChart.setBorderPaint(Color.WHITE);

		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 600));
		final XYPlot plot = xylineChart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.white); // 设置网格背景颜色

		plot.setDomainGridlinePaint(Color.white); // 设置网格竖线颜色
		plot.setRangeGridlinePaint(Color.white); // 设置网格横线颜色
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0)); // 设置曲线图与xy轴的距离

		// XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		// renderer.setSeriesPaint(0, Color.BLUE);
		// renderer.setBaseShapesVisible(false);
		// renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		// plot.setRenderer(renderer);

		XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setBaseShapesVisible(false); // 设置曲线是否显示数据点
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setPrecision(5); // 设置精度，大概意思是在源数据两个点之间插入5个点以拟合出一条平滑曲线
		plot.setRenderer(renderer);

		if (display) {
			ApplicationFrame af = new ApplicationFrame("wave chart");
			af.setContentPane(chartPanel);
			af.pack();
			RefineryUtilities.centerFrameOnScreen(af);
			af.setVisible(true);
		}
		return xylineChart;
	}

	/**
	 * save chart as JPEG or PNG file
	 * 
	 * @param chart
	 *            spline chart object
	 * @param outputPath
	 *            the image output path
	 * @param weight
	 * @param height
	 * @throws Exception
	 */
	public static void saveAsFile(JFreeChart chart, String outputPath, int weight, int height, int which) {
		FileOutputStream out = null;
		File outFile = new File(outputPath);
		if (!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		try {
			out = new FileOutputStream(outputPath);
			// save as png
			if (which == 0)
				ChartUtilities.writeChartAsPNG(out, chart, weight, height);
			// save as jpeg
			if (which == 1)
				ChartUtilities.writeChartAsJPEG(out, chart, weight, height);
			out.flush();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ---------------------------------------------------------------
	// this is the test code
	public static void main(String[] args) {
		ArrayList<Double> vd = new ArrayList<>();
		vd.add(1.0);
		vd.add(4.0);
		vd.add(4.5);
		vd.add(5.8);

		 //JFreeChart chart = init(vd,false);
		
		saveAsFile(init(vd,false),"D:/data/20201023/Tests/b.png",600,600,0);
		 
	}
	// end test code
	// -----------------------------------------------------------------

}
