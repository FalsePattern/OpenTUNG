package de.ecconia.java.opentung.components;

import de.ecconia.java.opentung.components.fragments.CubeBoard;
import de.ecconia.java.opentung.components.meta.CompContainer;
import de.ecconia.java.opentung.components.meta.ModelHolder;
import de.ecconia.java.opentung.libwrap.meshes.MeshTypeThing;
import de.ecconia.java.opentung.math.Vector3;

public class CompBoard extends CompContainer
{
	public static final ModelHolder modelHolder = new ModelHolder();
	
	static
	{
		modelHolder.setPlacementOffset(new Vector3(0.0, 0.0, 0.0));
		modelHolder.addSolid(new CubeBoard(new Vector3(0.0, 0.0, 0.0), new Vector3(2.0, 0.15, 2.0))); //1 gets replaced in shader. no color cause texture.
	}
	
	public static void initGL()
	{
		modelHolder.generateTestModel(ModelHolder.TestModelType.Board, false);
	}
	
	@Override
	public ModelHolder getModelHolder()
	{
		return modelHolder;
	}
	
	//### Non-Static ###
	
	private Vector3 color = new Vector3(195f / 255f, 195f / 255f, 195f / 255f);
	private int x, z;
	
	public CompBoard(CompContainer parent, int x, int z)
	{
		super(parent);
		
		this.x = x;
		this.z = z;
	}
	
	public void setColor(Vector3 color)
	{
		this.color = color;
	}
	
	public Vector3 getColor()
	{
		return color;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getZ()
	{
		return z;
	}
	
	//### GL-Stuff ###
	
	public void insertMeshData(float[] vertices, ModelHolder.IntHolder verticesIndex, int[] indices, ModelHolder.IntHolder indicesIndex, ModelHolder.IntHolder vertexCounter, MeshTypeThing type)
	{
		//TODO: This is super ungeneric, beware.
		CubeBoard shape = (CubeBoard) getModelHolder().getSolid().get(0);
		
		Vector3 color = this.color;
		if(type.colorISID())
		{
			int id = getRayID();
			int r = id & 0xFF;
			int g = (id & 0xFF00) >> 8;
			int b = (id & 0xFF0000) >> 16;
			color = new Vector3((float) r / 255f, (float) g / 255f, (float) b / 255f);
		}
		shape.generateBoardMeshEntry(vertices, verticesIndex, indices, indicesIndex, vertexCounter, x, z, color, getPosition(), getRotation(), type);
	}
}
