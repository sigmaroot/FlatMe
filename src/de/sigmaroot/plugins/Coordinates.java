package de.sigmaroot.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

public class Coordinates {

	private int startCoordX;
	private int startCoordY;
	private int simpleCoordX;
	private int simpleCoordY;
	private int endCoordX;
	private int endCoordY;
	private List<Integer> chunksX;
	private List<Integer> chunksZ;

	public Coordinates() {
		super();
		chunksX = new ArrayList<Integer>();
		chunksZ = new ArrayList<Integer>();
	}

	public int getStartCoordX() {
		return startCoordX;
	}

	public void setStartCoordX(int startCoordX) {
		this.startCoordX = startCoordX;
	}

	public int getStartCoordY() {
		return startCoordY;
	}

	public void setStartCoordY(int startCoordY) {
		this.startCoordY = startCoordY;
	}

	public int getSimpleCoordX() {
		return simpleCoordX;
	}

	public void setSimpleCoordX(int simpleCoordX) {
		this.simpleCoordX = simpleCoordX;
	}

	public int getSimpleCoordY() {
		return simpleCoordY;
	}

	public void setSimpleCoordY(int simpleCoordY) {
		this.simpleCoordY = simpleCoordY;
	}

	public int getEndCoordX() {
		return endCoordX;
	}

	public void setEndCoordX(int endCoordX) {
		this.endCoordX = endCoordX;
	}

	public int getEndCoordY() {
		return endCoordY;
	}

	public void setEndCoordY(int endCoordY) {
		this.endCoordY = endCoordY;
	}

	public List<Integer> getChunksX() {
		return chunksX;
	}

	public void setChunksX(List<Integer> chunksX) {
		this.chunksX = chunksX;
	}

	public List<Integer> getChunksZ() {
		return chunksZ;
	}

	public void setChunksZ(List<Integer> chunksZ) {
		this.chunksZ = chunksZ;
	}

	public void calculateAllChunks() {
		for (int i = startCoordX; i <= endCoordX; i++) {
			int chunkX = 0;
			chunkX = (int) Math.floor(i / 16);
			for (int j = startCoordY; j <= endCoordY; j++) {
				int chunkZ = 0;
				chunkZ = (int) Math.floor(j / 16);
				boolean found = false;
				for (int k = 0; k < chunksX.size(); k++) {
					if ((chunksX.get(k) == chunkX) && (chunksZ.get(k) == chunkZ)) {
						found = true;
						break;
					}
				}
				if (!found) {
					chunksX.add(chunkX);
					chunksZ.add(chunkZ);
				}
			}
		}
	}

	public void loadChunks(World world) {
		for (int i = 0; i < chunksX.size(); i++) {
			world.unloadChunk(chunksX.get(i), chunksZ.get(i), true);
			if (!world.isChunkLoaded(chunksX.get(i), chunksZ.get(i))) {
				world.loadChunk(chunksX.get(i), chunksZ.get(i));
			}
		}
	}

}
