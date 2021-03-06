package de.ecconia.java.opentung.libwrap;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class TextureWrapper
{
	private final int id;
	
	public TextureWrapper(BufferedImage image)
	{
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3);
		for(int y = 0; y < image.getHeight(); y++)
		{
			for(int x = 0; x < image.getWidth(); x++)
			{
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));    // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));     // Green component
				buffer.put((byte) (pixel & 0xFF));            // Blue component
			}
		}
		buffer.flip();
		
		id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//Anisotropic filtering: TODO: Check if available
//		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 2.5f);
//		System.out.println(GL.getCapabilities().GL_EXT_texture_filter_anisotropic);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, image.getWidth(), image.getHeight(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
		GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
	}
	
	public void activate()
	{
		//int index, int location
		//Not used cause only one texture in total. (At a time)
		//GL30.glActiveTexture(GL30.GL_TEXTURE0 + index);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
		//GL30.glUniform1i(location, GL30.GL_TEXTURE0 + index);
	}
}
