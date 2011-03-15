/**
 * Project: Assignment_03_2D-Plotting
 * File: Chart.java
 * Date: 2011-02-16
 * Time: 9:09:11 PM
 */
package org.trollop.Plot;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

/**
 * Creates a simple scaled line graph with x and y axis labels.
 * 
 * @author Steffen L. Norgren, A00683006
 * 
 */
public class Chart extends View {
	private Point viewSize = new Point();
	private Point margin = new Point(60, 40);
	private PointF minBounds = new PointF(Float.MAX_VALUE, Float.MAX_VALUE);
	private PointF maxBounds = new PointF(Float.MIN_VALUE, Float.MIN_VALUE);
	private PointF transMultiplier = new PointF();

	private boolean smooth;

	private float chartTitleSize = 18F;
	private float axisTitleSize = 14F;

	private String chartTitle;
	private String xAxisTitle;
	private String yAxisTitle;

	SortedMap<Float, Float> dataPoints;

	public Chart(Context context, SortedMap<Float, Float> dataPoints,
			String chartTitle, String xAxisTitle, String yAxisTitle,
			String xAxisUnits, String yAxisUnits, boolean smooth) {
		super(context);

		this.chartTitle = chartTitle;
		this.xAxisTitle = xAxisTitle + " (" + xAxisUnits + ")";
		this.yAxisTitle = yAxisTitle + " (" + yAxisUnits + ")";
		this.dataPoints = dataPoints;
		this.smooth = smooth;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		viewSize.set(this.getWidth(), this.getHeight());

		setDataBounds();
		autoScale();
		drawAxes(canvas);
		plot(canvas);
	}

	private void drawAxes(Canvas canvas) {
		Path path = new Path();
		Paint paint = new Paint();

		/* Set axes colours */
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1F);

		/* x axis at 0 intersect */
		path.moveTo(minBounds.x, 0);
		path.lineTo(maxBounds.x, 0);

		/* y axis at 0 intersect */
		path.moveTo(0, minBounds.y);
		path.lineTo(0, maxBounds.y);

		/* Transform and draw axes */
		Matrix m = new Matrix();
		path.offset(-minBounds.x, -minBounds.y);
		m.setScale(transMultiplier.x, -transMultiplier.y);
		path.transform(m);
		path.offset(margin.x, (viewSize.y - (margin.y * 2)) + margin.y);
		canvas.drawPath(path, paint);

		/* Draw chart title */
		paint.reset();
		paint.setColor(Color.YELLOW);
		paint.setTextAlign(Align.CENTER);
		paint.setAntiAlias(true);
		paint.setTextSize(chartTitleSize);
		canvas.drawText(chartTitle, viewSize.x / 2,
				(margin.y + chartTitleSize) / 2, paint);

		/* Set axes title colours */
		paint.setColor(Color.LTGRAY);
		paint.setTextSize(axisTitleSize);

		/* Draw x axis title */
		path.reset();
		path.moveTo(0, viewSize.y);
		path.lineTo(viewSize.x, viewSize.y);

		canvas.drawTextOnPath(xAxisTitle, path, 0, -axisTitleSize / 5, paint);

		/* Draw y axis title */
		path.reset();
		path.moveTo(0, viewSize.y);
		path.lineTo(0, 0);

		canvas.drawTextOnPath(yAxisTitle, path, 0, axisTitleSize, paint);
	}
	
	private void plot(Canvas canvas) {
		boolean initPoint = false;
		Path path = new Path();
		Paint paint = new Paint();

		paint.setColor(Color.CYAN);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1.5F);
		paint.setAntiAlias(true);

		// Iterate through the data set
		Set<?> set = dataPoints.entrySet();
		Iterator<?> iterPrev = set.iterator();
		Iterator<?> iterCur = set.iterator();

		if (!smooth) {
			while (iterCur.hasNext()) {

				Map.Entry<?, ?> map = (Entry<?, ?>) iterCur.next();

				if ((Float) map.getKey() >= minBounds.x
						&& (Float) map.getKey() <= maxBounds.x) {

					/* set initial point */
					if (!initPoint) {
						path.moveTo((Float) map.getKey(),
								(Float) map.getValue());
						initPoint = true;
					}

					path.lineTo((Float) map.getKey(), (Float) map.getValue());
				}
			}
		} else {
			iterCur.next(); /* move one point forward */

			while (iterCur.hasNext()) {
				PointF start = new PointF();
				PointF end = new PointF();
				PointF mid = new PointF();

				Map.Entry<?, ?> mapPrev = (Entry<?, ?>) iterPrev.next();
				Map.Entry<?, ?> mapCur = (Entry<?, ?>) iterCur.next();

				if ((Float) mapPrev.getKey() >= minBounds.x
						&& (Float) mapPrev.getKey() <= maxBounds.x) {

					/* set initial point */
					if (!initPoint) {
						path.moveTo((Float) mapPrev.getKey(),
								(Float) mapPrev.getValue());
						initPoint = true;
					}

					start.set((Float) mapPrev.getKey(),
							(Float) mapPrev.getValue());

					mid.set(((Float) mapPrev.getKey() + (Float) mapCur.getKey()) / 2,
							((Float) mapPrev.getValue() + (Float) mapCur
									.getValue()) / 2);

					end.set((Float) mapCur.getKey(), (Float) mapCur.getValue());

					path.quadTo((start.x + mid.x) / 2, start.y, mid.x, mid.y);
					path.quadTo((mid.x + end.x) / 2, end.y, end.x, end.y);
				}
			}
		}

		/* Transformations */
		Matrix m = new Matrix();
		path.offset(-minBounds.x, -minBounds.y);
		m.setScale(transMultiplier.x, -transMultiplier.y);
		path.transform(m);
		path.offset(margin.x + 1, (viewSize.y - (margin.y * 2)) + margin.y);
		canvas.drawPath(path, paint);
	}
	
	private void setTransMultiplier() {
		transMultiplier.set(
				(viewSize.x - margin.x) / Math.abs(maxBounds.x - minBounds.x),
				(viewSize.y - (margin.y * 2)) / Math.abs(maxBounds.y - minBounds.y));
	}

	private void setDataBounds() {
		// Iterate through the data set
		Set<?> set = dataPoints.entrySet();
		Iterator<?> iter = set.iterator();

		while (iter.hasNext()) {
			Map.Entry<?, ?> map = (Entry<?, ?>) iter.next();
			Float yValue = (Float) map.getValue();

			/* set min and max y axis bounds */
			if (yValue < minBounds.y)
				minBounds.set(dataPoints.firstKey(), yValue);
			if (yValue > maxBounds.y)
				maxBounds.set(dataPoints.lastKey(), yValue);
		}
	}
	
	private void autoScale() {
		NiceScale scaleXAxis = new NiceScale(minBounds.x, maxBounds.x);
		NiceScale scaleYAxis = new NiceScale(minBounds.y, maxBounds.y);
		
		minBounds.set((float) scaleXAxis.getNiceMin(), (float) scaleYAxis.getNiceMin());
		maxBounds.set((float) scaleXAxis.getNiceMax(), (float) scaleYAxis.getNiceMax());
		setTransMultiplier();
	}
}