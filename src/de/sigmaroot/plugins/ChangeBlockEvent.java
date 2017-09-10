package de.sigmaroot.plugins;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;

public class ChangeBlockEvent {

	private int x;
	private int y;
	private int z;
	private World world;
	private Material material;
	private byte data;
	private String[] args;

	public ChangeBlockEvent(int x, int y, int z, World world, Material material, byte data, String[] args) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.material = material;
		this.data = data;
		if (args != null) {
			this.args = args;
		}
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
		Block oldBlock = world.getBlockAt(loc);
		if (oldBlock.getState() instanceof Container) {
			Container blockInventory = (Container) oldBlock.getState();
			blockInventory.getInventory().clear();
		}
		world.getBlockAt(loc).setType(material);
		world.getBlockAt(loc).setData(data);
		if (material == Material.WALL_SIGN) {
			Sign thisSign = (Sign) world.getBlockAt(loc).getState();
			thisSign.setLine(0, args[0]);
			thisSign.setLine(1, args[1]);
			thisSign.setLine(2, args[2]);
			org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(Material.WALL_SIGN);
			switch (args[3]) {
			case "north":
				matSign.setFacingDirection(BlockFace.NORTH);
				break;
			case "east":
				matSign.setFacingDirection(BlockFace.EAST);
				break;
			case "south":
				matSign.setFacingDirection(BlockFace.SOUTH);
				break;
			case "west":
				matSign.setFacingDirection(BlockFace.WEST);
				break;
			default:
				matSign.setFacingDirection(BlockFace.NORTH);
				break;
			}
			thisSign.setData(matSign);
			thisSign.update();
		}
	}

}
