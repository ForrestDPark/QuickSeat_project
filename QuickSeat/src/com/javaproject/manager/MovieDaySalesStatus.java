package com.javaproject.manager;

import java.awt.EventQueue;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.javaproject.base.ShareVar;
import com.javaproject.managerfunction.DaoUserStatistics;
import com.javaproject.managerfunction.DtoWDH;

// 차트에 대한 rnd, 일단은 선그래프만 나오지만 bar그래프로 바꿀 수도 있게 해볼 예정
public class MovieDaySalesStatus extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MovieDaySalesStatus frame = new MovieDaySalesStatus();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MovieDaySalesStatus() {
		
		setBounds(ShareVar.managerXlocation+200, ShareVar.managerYlocation-100, ShareVar.managerXsize, ShareVar.managerYsize);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);


		// 그래프 생성
		JFreeChart chart = createChart(createDataset());

		// 그래프를 패널에 추가
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setFont(new Font("BM Dohyeon", Font.PLAIN, 12));
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
		setContentPane(chartPanel);

		Font font = new Font("BM Dohyeon", Font.PLAIN, 12); // 원하는 폰트 및 스타일 설정
		TextTitle chartTitle = chart.getTitle(); // 차트 제목 폰트 설정
		Font titleFont = new Font("BM Dohyeon", Font.PLAIN, 20); // 원하는 폰트 및 스타일 설정
		chartTitle.setFont(titleFont);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();

		// 차트 렌더러(Renderer)를 가져와서 값 표시 설정, 바 위에 숫자를 추가해주기 위해 필요
		BarRenderer renderer = (BarRenderer) plot.getRenderer();

		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);

		//바 위의 값 표시 위치 설정
		renderer.setDefaultPositiveItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
		renderer.setDefaultNegativeItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER));

		// 값 표시 폰트 설정
		renderer.setDefaultItemLabelFont(font);

		 // Y축 폰트 설정
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setTickLabelFont(font);
		yAxis.setLabelFont(font);

		 // X축 폰트 설정
		CategoryAxis xAxis = plot.getDomainAxis();
		xAxis.setLabelFont(font);
		xAxis.setTickLabelFont(font);

		// 범례 폰트 설정
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(font);

	}

	private CategoryDataset createDataset() {
		
		// DefaultCategoryDataset이 아닌 다른 Class를 고르면 dataset 표현방법을 바꿀 수 있음.
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// 데이터 추가
		DaoUserStatistics dao = new DaoUserStatistics();
		ArrayList<DtoWDH> dto = dao.pricePerDay();
		
		// 1주일 단위로 표 만들기
		
        LocalDate localDate = LocalDate.of(Integer.parseInt(ShareVar.year),Integer.parseInt(ShareVar.month), 1);
        int daysInMonth = localDate.lengthOfMonth();
//        System.out.println(Integer.parseInt(ShareVar.year));
//        System.out.println(Integer.parseInt(ShareVar.month));
//        System.out.println(daysInMonth);
 
		for(int i = daysInMonth-1; i >= 0; i--) {
			int date = daysInMonth-i;
			String day = ShareVar.month + "-" + Integer.toString(date);
			int sumPrice = 0;
			
			// 가져온 day와 현재 day가 일치할 경우 sumPrice에 가격 추가
			for(int j = 0; j < dto.size(); j++) {
				String day1 = dto.get(j).getResv_date().substring(3,8);
				if(day1.equals(day)) {
					sumPrice += dto.get(j).getTicket_price();
				}
			}
			dataset.addValue(sumPrice, "이번년도", Integer.toString(date));
		}

		return dataset;
	}

	private JFreeChart createChart(CategoryDataset dataset) {
		return ChartFactory.createBarChart("일별 매출 현황", // 차트 제목
				"", // X 축 레이블
				"만원", // Y 축 레이블
				dataset // 데이터셋
		);
	}


}
