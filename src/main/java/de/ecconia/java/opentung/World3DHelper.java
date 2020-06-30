package de.ecconia.java.opentung;

import de.ecconia.java.opentung.components.fragments.CubeFull;
import de.ecconia.java.opentung.components.fragments.Meshable;
import de.ecconia.java.opentung.components.meta.Component;
import de.ecconia.java.opentung.components.meta.Part;
import de.ecconia.java.opentung.libwrap.Matrix;
import de.ecconia.java.opentung.libwrap.ShaderProgram;
import de.ecconia.java.opentung.libwrap.vaos.GenericVAO;
import de.ecconia.java.opentung.math.Vector3;

public class World3DHelper
{
	public static void drawStencilComponent(ShaderProgram justShape, GenericVAO cubeVAO, Component component, float[] view)
	{
		justShape.use();
		justShape.setUniform(1, view);
		justShape.setUniformV4(3, new float[] {0,0,0,0});
		Matrix matrix = new Matrix();
		Vector3 placementOffset = component.getModelHolder().getPlacementOffset();
		for(Meshable meshable : component.getModelHolder().getPegModels())
		{
			drawCubeFull(justShape, cubeVAO, (CubeFull) meshable, component, placementOffset, matrix);
		}
		for(Meshable meshable : component.getModelHolder().getBlotModels())
		{
			drawCubeFull(justShape, cubeVAO, (CubeFull) meshable, component, placementOffset, matrix);
		}
		for(Meshable meshable : component.getModelHolder().getColorables())
		{
			drawCubeFull(justShape, cubeVAO, (CubeFull) meshable, component, placementOffset, matrix);
		}
		for(Meshable meshable : component.getModelHolder().getSolid())
		{
			drawCubeFull(justShape, cubeVAO, (CubeFull) meshable, component, placementOffset, matrix);
		}
		for(Meshable meshable : component.getModelHolder().getConductors())
		{
			drawCubeFull(justShape, cubeVAO, (CubeFull) meshable, component, placementOffset, matrix);
		}
	}
	
	public static void drawCubeFull(ShaderProgram justShape, GenericVAO cubeVAO, CubeFull cube, Part part, Vector3 placementOffset, Matrix matrix)
	{
		//TBI: maybe optimize this a bit more, its quite a lot annoying matrix operations.
		matrix.identity();
		Vector3 position = part.getPosition();
		matrix.translate((float) position.getX(), (float) position.getY(), (float) position.getZ());
		Matrix rotMat = new Matrix(part.getRotation().createMatrix());
		matrix.multiply(rotMat);
		Vector3 size = cube.getSize();
		if(cube.getMapper() != null)
		{
			size = cube.getMapper().getMappedSize(size, part);
		}
		position = cube.getPosition().add(placementOffset);
		matrix.translate((float) position.getX(), (float) position.getY(), (float) position.getZ());
		matrix.scale((float) size.getX(), (float) size.getY(), (float) size.getZ());
		justShape.setUniform(2, matrix.getMat());
		
		cubeVAO.use();
		cubeVAO.draw();
	}
}
