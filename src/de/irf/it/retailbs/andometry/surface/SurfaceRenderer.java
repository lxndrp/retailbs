package de.irf.it.retailbs.andometry.surface;


import android.opengl.GLSurfaceView;
import android.util.Pair;
import de.irf.it.retailbs.andometry.AbstractOdometryServiceConsumerActivity;
import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class SurfaceRenderer
		implements GLSurfaceView.Renderer {

	/**
	 * 
	 */
	private final AbstractOdometryServiceConsumerActivity parent;

	/**
	 * 
	 */
	private final Grid grid;

	/**
	 * 
	 */
	private final Marker marker;

	/**
	 * 
	 */
	public SurfaceRenderer(AbstractOdometryServiceConsumerActivity parent) {
		this.parent = parent;
		this.grid = new Grid();
		this.marker = new Marker();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		/*
		 * Clear the screen and initialize.
		 */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		/*
		 * Draw objects.
		 */
		gl.glTranslatef(0, 0, -2.0f);
		this.marker.draw(gl); // phone marker

		Pair<Vector3D, Rotation> currentPosition = this.parent.getGenerator()
				.getCurrentPosition();

		float[] m4x4 = this.convertRotationToGLMatrix(currentPosition.second.revert());
		gl.glMultMatrixf(m4x4, 0);
		gl.glTranslatef(-( float )currentPosition.first.getX(),
				-( float )currentPosition.first.getY(),
				-( float )currentPosition.first.getZ());

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		this.grid.draw(gl); // grid
		this.marker.draw(gl); // world marker
	}

	/**
	 * @param r
	 * @return
	 */
	private float[] convertRotationToGLMatrix(Rotation r) {
		float[] m4x4 = new float[16];
		double[][] m3x3 = r.getMatrix();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				m4x4[4 * i + j] = ( float )m3x3[i][j];
			}
			m4x4[4 * i + 3] = m4x4[3 * 4 + i] = 0;
		}
		m4x4[3 * 4 + 3] = 1;
		return m4x4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		/*
		 * Update viewport.
		 */
		gl.glViewport(0, 0, width, height);

		/*
		 * Set projection matrix.
		 */
		float ratio = ( float )width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 30);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * Favor performance over quality.
		 */
		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Perform one-time OpenGL initialization.
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(0, 0, 0, 0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}
}
