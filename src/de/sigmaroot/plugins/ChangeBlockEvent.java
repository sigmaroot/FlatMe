package de.sigmaroot.plugins;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class ChangeBlockEvent {

	private int x;
	private int y;
	private int z;
	private World world;
	private Material material;
	private byte data;

	public ChangeBlockEvent(int x, int y, int z, World world, Material material, byte data) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.material = material;
		this.data = data;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@SuppressWarnings("deprecation")
	public void doEvent() {
		Location loc = new Location(world, x, y, z);
		world.getBlockAt(loc).setType(material);
		world.getBlockAt(loc).setData(data);
	}

}
