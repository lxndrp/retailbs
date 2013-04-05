package de.irf.it.retailbs.andometry.surface;


import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


class Marker {

	/**
	 * 
	 */
	private ByteBuffer indexBuffer;

	/**
	 * 
	 */
	private FloatBuffer vertexBuffer;

	/**
	 * 
	 */
	private FloatBuffer colorBuffer;

	/**
	 * 
	 */
	public Marker() {
		float vertices[] =
			{	0f, 0f, 0.01f,
				1f, 0f, 0.01f,
				0f, 0f, 0.01f,
				0f, 1f, 0.01f,
				0f, 0f, 0.01f,
				0f, 0f, 1.01f };

		float colors[] =
			{	1f, 0f, 0f, 1f,
				1f, 0f, 0f, 1f,
				0f, 1f, 0f, 1f,
				0f, 1f, 0f, 1f,
				0f, 0f, 1f, 1f,
				0f, 0f, 1f, 1f };

		byte indices[] =
			{ 0, 1, 2, 3, 4, 5, 6 };

		// Buffers to be passed to gl*Pointer() functions
		// must be direct, i.e., they must be placed on the
		// native heap where the garbage collector cannot
		// move them.
		//
		// Buffers with multi-byte datatypes (e.g., short, int, float)
		// must have their byte order set to native order

		ByteBuffer vbb = ByteBuffer
				.allocateDirect(vertices.length * Float.SIZE);
		vbb.order(ByteOrder.nativeOrder());
		this.vertexBuffer = vbb.asFloatBuffer();
		this.vertexBuffer.put(vertices);
		this.vertexBuffer.position(0);

		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * Float.SIZE);
		cbb.order(ByteOrder.nativeOrder());
		this.colorBuffer = cbb.asFloatBuffer();
		this.colorBuffer.put(colors);
		this.colorBuffer.position(0);

		this.indexBuffer = ByteBuffer.allocateDirect(indices.length);
		this.indexBuffer.put(indices);
		this.indexBuffer.position(0);
	}

	/**
	 * @param gl
	 */
	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colorBuffer);
		gl.glDrawElements(GL10.GL_LINES, 6, GL10.GL_UNSIGNED_BYTE,
				this.indexBuffer);
	}
}
