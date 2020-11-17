package com.yhy.eventjudgement;


import java.awt.Color;
import java.io.File;
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

public class WaveChart{
	
	private static XYSeriesCollection dataset = null;
	private static JFreeChart xylineChart= null;
	
	
	private  WaveChart() {
		
	}

	public static JFreeChart init(ArrayList<Double> vd,boolean display) {
		
		XYSeries coordinates = new XYSeries("coordinates");
    	for(int i=0;i<vd.size();i++) {
    		coordinates.add(i, vd.get(i));
    	}
    	dataset = new XYSeriesCollection();
    	dataset.addSeries(coordinates);
		
		xylineChart = ChartFactory.createXYLineChart("wave chart", "X", "Y", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		xylineChart.setBackgroundPaint(Color.white);
        xylineChart.setBorderVisible(true);
        xylineChart.setBorderPaint(Color.BLACK);
        
		ChartPanel chartPanel = new ChartPanel(xylineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 450));
		final XYPlot plot = xylineChart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		plot.setRenderer(renderer);
		
		if(display) {
			ApplicationFrame af = new ApplicationFrame("wave chart");
			af.setContentPane(chartPanel);
			af.pack();
			RefineryUtilities.centerFrameOnScreen(af);
			af.setVisible(true);
		}
		return xylineChart;
	}
	
	
	/**
	 * 将图表保存为PNG、JPEG图片
	 * @param chart  折线图对象
	 * @param outputPath 文件保存路径, 包含文件名
	 * @param weight  宽
	 * @param height 高
	 * @throws Exception
	 */
	public static void saveAsFile(JFreeChart chart, String outputPath, int weight, int height,int which)throws Exception {      
		FileOutputStream out = null;      
		File outFile = new File(outputPath);      
		if (!outFile.getParentFile().exists()) {      
			outFile.getParentFile().mkdirs();      
		}      
		out = new FileOutputStream(outputPath);      
		// save as png
		if(which == 0)
			ChartUtilities.writeChartAsPNG(out, chart, weight, height);      
		// save as jpeg
		if(which == 1)
			ChartUtilities.writeChartAsJPEG(out, chart, weight, height);      
		out.flush();      
		if (out != null) {      
			try {      
				out.close();      
			} catch (IOException e) {      
				e.printStackTrace();
			}      
		}      
	}

	//---------------------------------------------------------------
	//this is the test code
	public static void main(String[] args) {
		ArrayList<Double> vd = new ArrayList<>();
		vd.add(1.0);
		vd.add(4.0);
		vd.add(4.5);
		vd.add(5.8);
		JFreeChart chart = init(vd,false);
		
		try {
			saveAsFile(chart,"D:/data/20201023/Tests/b.png",400,400,0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//end test code
	//-----------------------------------------------------------------

}
