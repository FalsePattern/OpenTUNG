package de.ecconia.java.opentung.components;

import de.ecconia.java.opentung.components.fragments.Color;
import de.ecconia.java.opentung.components.fragments.CubeFull;
import de.ecconia.java.opentung.components.fragments.CubeOpen;
import de.ecconia.java.opentung.components.fragments.Direction;
import de.ecconia.java.opentung.components.meta.CompContainer;
import de.ecconia.java.opentung.components.meta.Component;
import de.ecconia.java.opentung.components.meta.ModelHolder;
import de.ecconia.java.opentung.math.Vector3;

public class CompDisplay extends Component
{
	public static final Color offColor = Color.rgb(32, 32, 32);
	
	public static final ModelHolder modelHolder = new ModelHolder();
	
	static
	{
		modelHolder.setPlacementOffset(new Vector3(0.0, 0.0, 0.0));
		modelHolder.addSolid(new CubeFull(new Vector3(0.0, 0.48, 0.0), new Vector3(0.3, 0.3, 0.3), offColor));
		modelHolder.addConnector(new CubeOpen(new Vector3(0.0, 0.24, 0.0), new Vector3(0.09, 0.48, 0.09), Direction.YPos));
	}
	
	public static void initGL()
	{
		modelHolder.generateTestModel(ModelHolder.TestModelType.Simple);
	}
	
	@Override
	public ModelHolder getModelHolder()
	{
		return modelHolder;
	}
	
	//### Non-Static ###
	
	private Vector3 colorRaw;
	
	public CompDisplay(CompContainer parent)
	{
		super(parent);
	}
	
	public void setColorRaw(Vector3 colorRaw)
	{
		this.colorRaw = colorRaw;
	}
	
	public Vector3 getColorRaw()
	{
		return colorRaw;
	}
}
