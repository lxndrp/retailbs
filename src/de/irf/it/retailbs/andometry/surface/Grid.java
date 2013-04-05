package de.irf.it.retailbs.andometry.surface;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


class Grid {

	/**
	 * 
	 */
	private static final int LINES_PER_DIMENSION = 63;

	/**
	 * 
	 */
	private static final float SPACING = 1.0f;

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
	public Grid() {

		// two points per line, two sets of lines
		float vertices[] = new float[2 * 2 * Grid.LINES_PER_DIMENSION * 3];
		float colors[] = new float[2 * 2 * Grid.LINES_PER_DIMENSION * 4];
		byte indices[] = new byte[4 * Grid.LINES_PER_DIMENSION];

		float max = Grid.LINES_PER_DIMENSION / 2 * Grid.SPACING;

		for (int i = 0; i < Grid.LINES_PER_DIMENSION; i++) {
			// start 1
			vertices[i * 4 * 3 + 0] = (i - Grid.LINES_PER_DIMENSION / 2)
					* Grid.SPACING; // x
			vertices[i * 4 * 3 + 1] = max; // y
			vertices[i * 4 * 3 + 2] = 0; // z
			// end 1
			vertices[i * 4 * 3 + 3] = (i - Grid.LINES_PER_DIMENSION / 2)
					* Grid.SPACING; // x
			vertices[i * 4 * 3 + 4] = -max; // y
			vertices[i * 4 * 3 + 5] = 0; // z
			// start 2
			vertices[i * 4 * 3 + 6] = max; // x
			vertices[i * 4 * 3 + 7] = (i - Grid.LINES_PER_DIMENSION / 2)
					* Grid.SPACING; // y
			vertices[i * 4 * 3 + 8] = 0; // z
			// end 2
			vertices[i * 4 * 3 + 9] = -max; // x
			vertices[i * 4 * 3 + 10] = (i - Grid.LINES_PER_DIMENSION / 2)
					* Grid.SPACING; // y
			vertices[i * 4 * 3 + 11] = 0; // z

			// put all colors the same for the beginning
			for (int j = 0; j < 4; j++) {
				colors[i * 4 * 4 + j * 4 + 0] = 1;
				colors[i * 4 * 4 + j * 4 + 1] = 1;
				colors[i * 4 * 4 + j * 4 + 2] = 1;
				colors[i * 4 * 4 + j * 4 + 3] = 1;
			}

			// the vertices are just put one behind the other
			for (int j = 0; j < 4; j++) {
				indices[4 * i + j] = ( byte )(4 * i + j);
			}

		}

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

		// ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length*4);
		// ibb.order(ByteOrder.nativeOrder());
		// mIndexBuffer = ibb.asIntBuffer();
		// mIndexBuffer.put(indices);
		// mIndexBuffer.position(0);

		this.indexBuffer = ByteBuffer.allocateDirect(indices.length);
		this.indexBuffer.put(indices);
		this.indexBuffer.position(0);
	}

	/**
	 * @param gl
	 */
	public void draw(GL10 gl) {
		// gl.glFrontFace(gl.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colorBuffer);
		gl.glDrawElements(GL10.GL_LINES, 4 * Grid.LINES_PER_DIMENSION,
				GL10.GL_UNSIGNED_BYTE, this.indexBuffer);
	}
}
