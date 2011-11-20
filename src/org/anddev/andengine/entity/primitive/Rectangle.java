package org.anddev.andengine.entity.primitive;

import java.nio.FloatBuffer;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.opengl.shader.PositionColorShaderProgram;
import org.anddev.andengine.opengl.shader.util.constants.ShaderProgramConstants;
import org.anddev.andengine.opengl.vbo.HighPerformanceVertexBufferObject;
import org.anddev.andengine.opengl.vbo.IVertexBufferObject;
import org.anddev.andengine.opengl.vbo.LowMemoryVertexBufferObject;
import org.anddev.andengine.opengl.vbo.VertexBufferObject.DrawType;
import org.anddev.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute;
import org.anddev.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.anddev.andengine.opengl.vbo.attribute.VertexBufferObjectAttributesBuilder;

import android.opengl.GLES20;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 12:18:49 - 13.03.2010
 */
public class Rectangle extends RectangularShape {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int VERTEX_INDEX_X = 0;
	public static final int VERTEX_INDEX_Y = Rectangle.VERTEX_INDEX_X + 1;
	public static final int COLOR_INDEX = Rectangle.VERTEX_INDEX_Y + 1;

	public static final int VERTEX_SIZE = 2 + 1;
	public static final int VERTICES_PER_RECTANGLE = 4;
	public static final int RECTANGLE_SIZE = Rectangle.VERTEX_SIZE * Rectangle.VERTICES_PER_RECTANGLE;

	public static final VertexBufferObjectAttributes VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT = new VertexBufferObjectAttributesBuilder(2)
		.add(ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION, 2, GLES20.GL_FLOAT, false)
		.add(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION, ShaderProgramConstants.ATTRIBUTE_COLOR, 4, GLES20.GL_UNSIGNED_BYTE, true)
		.build();

	// ===========================================================
	// Fields
	// ===========================================================

	protected final IRectangleVertexBufferObject mRectangleVertexBufferObject;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Uses a default {@link HighPerformanceRectangleVertexBufferObject} in {@link DrawType#STATIC} with the {@link VertexBufferObjectAttribute}s: {@link Rectangle#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Rectangle(final float pX, final float pY, final float pWidth, final float pHeight) {
		this(pX, pY, pWidth, pHeight, DrawType.STATIC);
	}

	/**
	 * Uses a default {@link HighPerformanceRectangleVertexBufferObject} with the {@link VertexBufferObjectAttribute}s: {@link Rectangle#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Rectangle(final float pX, final float pY, final float pWidth, final float pHeight, final DrawType pDrawType) {
		this(pX, pY, pWidth, pHeight, new HighPerformanceRectangleVertexBufferObject(Rectangle.RECTANGLE_SIZE, pDrawType, true, Rectangle.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
	}

	public Rectangle(final float pX, final float pY, final float pWidth, final float pHeight, final IRectangleVertexBufferObject pRectangleVertexBufferObject) {
		super(pX, pY, pWidth, pHeight, PositionColorShaderProgram.getInstance());

		this.mRectangleVertexBufferObject = pRectangleVertexBufferObject;

		this.onUpdateVertices();
		this.onUpdateColor();

		this.setBlendingEnabled(true);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public IRectangleVertexBufferObject getVertexBufferObject() {
		return this.mRectangleVertexBufferObject;
	}

	@Override
	protected void preDraw(final Camera pCamera) {
		super.preDraw(pCamera);

		this.mRectangleVertexBufferObject.bind(this.mShaderProgram);
	}

	@Override
	protected void draw(final Camera pCamera) {
		this.mRectangleVertexBufferObject.draw(GLES20.GL_TRIANGLE_STRIP, Rectangle.VERTICES_PER_RECTANGLE);
	}

	@Override
	protected void postDraw(final Camera pCamera) {
		this.mRectangleVertexBufferObject.unbind(this.mShaderProgram);

		super.postDraw(pCamera);
	}

	@Override
	protected void onUpdateColor() {
		this.mRectangleVertexBufferObject.onUpdateColor(this);
	}

	@Override
	protected void onUpdateVertices() {
		this.mRectangleVertexBufferObject.onUpdateVertices(this);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IRectangleVertexBufferObject extends IVertexBufferObject {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onUpdateColor(final Rectangle pRectangle);
		public void onUpdateVertices(final Rectangle pRectangle);
	}

	public static class HighPerformanceRectangleVertexBufferObject extends HighPerformanceVertexBufferObject implements IRectangleVertexBufferObject {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		// ===========================================================
		// Constructors
		// ===========================================================

		public HighPerformanceRectangleVertexBufferObject(final int pCapacity, final DrawType pDrawType, final boolean pManaged, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
			super(pCapacity, pDrawType, pManaged, pVertexBufferObjectAttributes);
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public void onUpdateColor(final Rectangle pRectangle) {
			final float[] bufferData = this.mBufferData;

			final float packedColor = pRectangle.getColor().getPacked();

			bufferData[0 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX] = packedColor;
			bufferData[1 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX] = packedColor;
			bufferData[2 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX] = packedColor;
			bufferData[3 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX] = packedColor;

			this.setDirtyOnHardware();
		}

		@Override
		public void onUpdateVertices(final Rectangle pRectangle) {
			final float[] bufferData = this.mBufferData;

			final float x = 0;
			final float y = 0;
			final float x2 = pRectangle.getWidth(); // TODO Optimize with field access?
			final float y2 = pRectangle.getHeight(); // TODO Optimize with field access?

			bufferData[0 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X] = x;
			bufferData[0 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y] = y;

			bufferData[1 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X] = x;
			bufferData[1 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y] = y2;

			bufferData[2 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X] = x2;
			bufferData[2 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y] = y;

			bufferData[3 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X] = x2;
			bufferData[3 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y] = y2;

			this.setDirtyOnHardware();
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}

	public static class LowMemoryRectangleVertexBufferObject extends LowMemoryVertexBufferObject implements IRectangleVertexBufferObject {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		// ===========================================================
		// Constructors
		// ===========================================================

		public LowMemoryRectangleVertexBufferObject(final int pCapacity, final DrawType pDrawType, final boolean pManaged, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
			super(pCapacity, pDrawType, pManaged, pVertexBufferObjectAttributes);
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public void onUpdateColor(final Rectangle pRectangle) {
			final FloatBuffer bufferData = this.mFloatBuffer;

			final float packedColor = pRectangle.getColor().getPacked();

			bufferData.put(0 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX, packedColor);
			bufferData.put(1 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX, packedColor);
			bufferData.put(2 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX, packedColor);
			bufferData.put(3 * Rectangle.VERTEX_SIZE + Rectangle.COLOR_INDEX, packedColor);

			this.setDirtyOnHardware();
		}

		@Override
		public void onUpdateVertices(final Rectangle pRectangle) {
			final FloatBuffer bufferData = this.mFloatBuffer;

			final float x = 0;
			final float y = 0;
			final float x2 = pRectangle.getWidth();
			final float y2 = pRectangle.getHeight();

			bufferData.put(0 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X, x);
			bufferData.put(0 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y, y);

			bufferData.put(1 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X, x);
			bufferData.put(1 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y, y2);

			bufferData.put(2 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X, x2);
			bufferData.put(2 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y, y);

			bufferData.put(3 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_X, x2);
			bufferData.put(3 * Rectangle.VERTEX_SIZE + Rectangle.VERTEX_INDEX_Y, y2);

			this.setDirtyOnHardware();
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
