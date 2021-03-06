package de.ecconia.java.opentung.libwrap.meshes;

import de.ecconia.java.opentung.components.CompSnappingPeg;
import de.ecconia.java.opentung.components.conductor.Blot;
import de.ecconia.java.opentung.components.conductor.CompWireRaw;
import de.ecconia.java.opentung.components.conductor.Peg;
import de.ecconia.java.opentung.components.meta.Component;
import de.ecconia.java.opentung.components.meta.ModelHolder;
import de.ecconia.java.opentung.libwrap.ShaderProgram;
import de.ecconia.java.opentung.libwrap.vaos.GenericVAO;
import de.ecconia.java.opentung.libwrap.vaos.LargeGenericVAO;
import de.ecconia.java.opentung.simulation.Clusterable;
import de.ecconia.java.opentung.simulation.SimulationManager;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL30;

public class ConductorMesh
{
	private final ShaderProgram solidMeshShader;
	
	private GenericVAO vao;
	
	//TODO: Apply check, that the ID's never get over the size below *32
	//TODO: Apply check, that the amount of array positions gets generated automatically.
	private final int[] falseDataArray = new int[1016 * 4];
	
	public ConductorMesh(List<Component> componentsToRender, List<CompWireRaw> wiresToRender, SimulationManager simulation, boolean maybeClusterless)
	{
		this.solidMeshShader = new ShaderProgram("mesh/meshConductor");
		simulation.setConnectorMeshStates(falseDataArray);
		
		update(componentsToRender, wiresToRender, maybeClusterless);
		
		//By clusters:
		Arrays.fill(falseDataArray, 0);
	}
	
	public void update(List<Component> componentsToRender, List<CompWireRaw> wiresToRender)
	{
		update(componentsToRender, wiresToRender, false);
	}
	
	public void update(List<Component> componentsToRender, List<CompWireRaw> wiresToRender, boolean maybeClusterless)
	{
		if(vao != null)
		{
			vao.unload();
		}
		
		int verticesAmount = wiresToRender.size() * 4 * 4 * (3 + 3);
		int indicesAmount = wiresToRender.size() * 4 * (2 * 3);
		for(Component component : componentsToRender)
		{
			for(Peg peg : component.getPegs())
			{
				verticesAmount += peg.getWholeMeshEntryVCount(MeshTypeThing.Conductor);
				indicesAmount += peg.getWholeMeshEntryICount(MeshTypeThing.Conductor);
			}
			for(Blot blot : component.getBlots())
			{
				verticesAmount += blot.getWholeMeshEntryVCount(MeshTypeThing.Conductor);
				indicesAmount += blot.getWholeMeshEntryICount(MeshTypeThing.Conductor);
			}
		}
		
		float[] vertices = new float[verticesAmount];
		int[] indices = new int[indicesAmount];
		int[] clusterIDs = new int[indicesAmount / 6 * 4];
		
		ModelHolder.IntHolder clusterIDIndex = new ModelHolder.IntHolder();
		ModelHolder.IntHolder vertexCounter = new ModelHolder.IntHolder();
		ModelHolder.IntHolder verticesOffset = new ModelHolder.IntHolder();
		ModelHolder.IntHolder indicesOffset = new ModelHolder.IntHolder();
		for(CompWireRaw wire : wiresToRender)
		{
			wire.insertMeshData(vertices, verticesOffset, indices, indicesOffset, vertexCounter, MeshTypeThing.Conductor);
			//TODO: Ungeneric:
			int clusterID = getClusterID(wire, maybeClusterless);
			//Wire has 4 Sides, each 4 vertices: 16
			for(int i = 0; i < 4 * 4; i++)
			{
				clusterIDs[clusterIDIndex.getAndInc()] = clusterID;
			}
		}
		for(Component comp : componentsToRender)
		{
			if(comp instanceof CompSnappingPeg)
			{
				continue;
			}
			//TODO: Ungeneric:
			for(Peg peg : comp.getPegs())
			{
				peg.insertMeshData(vertices, verticesOffset, indices, indicesOffset, vertexCounter, MeshTypeThing.Conductor);
				int clusterID = getClusterID(peg, maybeClusterless);
				for(int i = 0; i < peg.getModel().getFacesCount() * 4; i++)
				{
					clusterIDs[clusterIDIndex.getAndInc()] = clusterID;
				}
			}
			for(Blot blot : comp.getBlots())
			{
				blot.insertMeshData(vertices, verticesOffset, indices, indicesOffset, vertexCounter, MeshTypeThing.Conductor);
				int clusterID = getClusterID(blot, maybeClusterless);
				for(int i = 0; i < blot.getModel().getFacesCount() * 4; i++)
				{
					clusterIDs[clusterIDIndex.getAndInc()] = clusterID;
				}
			}
		}
		
		vao = new ConductorMeshVAO(vertices, indices, clusterIDs);
	}
	
	private int getClusterID(Clusterable clusterable, boolean maybeClusterless)
	{
		if(maybeClusterless)
		{
			return 0;
		}
		if(clusterable.hasCluster())
		{
			return clusterable.getCluster().getId();
		}
		else
		{
			throw new RuntimeException("Found Clusterable without a cluster :/");
		}
	}
	
	public void draw(float[] view)
	{
		solidMeshShader.use();
		solidMeshShader.setUniform(1, view);
		solidMeshShader.setUniformArray(2, falseDataArray);
		solidMeshShader.setUniform(3, view);
		vao.use();
		vao.draw();
	}
	
	public void updateProjection(float[] projection)
	{
		solidMeshShader.use();
		solidMeshShader.setUniform(0, projection);
	}
	
	private static class ConductorMeshVAO extends LargeGenericVAO
	{
		protected ConductorMeshVAO(float[] vertices, int[] indices, int[] ids)
		{
			super(vertices, indices, ids);
		}
		
		@Override
		protected void uploadMoreData(Object... extra)
		{
			System.out.println(getClass().getSimpleName() + " E: " + ((int[]) extra[0]).length);
			int vboID = GL30.glGenBuffers();
			GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
			GL30.glBufferData(GL30.GL_ARRAY_BUFFER, (int[]) extra[0], GL30.GL_STATIC_DRAW);
			//ClusterID:
			GL30.glVertexAttribIPointer(2, 1, GL30.GL_UNSIGNED_INT, Integer.BYTES, 0);
			GL30.glEnableVertexAttribArray(2);
		}
		
		@Override
		protected void init()
		{
			//Position:
			GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 6 * Float.BYTES, 0);
			GL30.glEnableVertexAttribArray(0);
			//Normal:
			GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
			GL30.glEnableVertexAttribArray(1);
		}
	}
}
