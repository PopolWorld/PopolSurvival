package me.nathanfallet.popolsurvival.utils;

import java.util.ArrayList;
import java.util.List;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.api.APIChunk;
import me.nathanfallet.popolserver.api.APIMessage;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;

public class PopolRegion {

    // Properties
    private Long x;
    private Long z;
    private List<APIChunk> chunks;

    // Constructor
    public PopolRegion(Long x, Long z, List<APIChunk> chunks) {
        this.x = x;
        this.z = z;
        this.chunks = chunks;
    }

    // Retieve x coordinate
    public Long getX() {
        return x;
    }

    // Retrieve z coordinate
    public Long getZ() {
        return z;
    }

    // Get chunks in this region
    public List<APIChunk> getChunks() {
        // Create the list if needed
        if (chunks == null) {
            chunks = new ArrayList<>();
        }

        // Return chunks
        return chunks;
    }

    // Get chunk at specified coordinates
    // Coordinates are world coordinates, not region coordinates
    public APIChunk getChunk(Long x, Long z) {
        // Check that coordinates are for this region
        if (getX().equals(x >> 5) || getZ().equals(z >> 5)) {
            return null;
        }

        // Filter chunks
        for (APIChunk chunk : getChunks()) {
            if (chunk.x.equals(x) && chunk.z.equals(z)) {
                return chunk;
            }
        }

        // Not found, return null
        return null;
    }

    // Claim a chunk at some coordinates for a team
    public void claimChunk(Long x, Long z, PopolTeam team, final ChunkLoaderHandler handler) {
        // Check that coordinates are for this region
        if (getX().equals(x >> 5) || getZ().equals(z >> 5)) {
            handler.chunkLoaded(null);
            return;
        }

        // Fetch API
        PopolServer.getInstance().getConnector().postChunk(x, z, team.getId(), new CompletionHandler<APIChunk>(){
			@Override
			public void completionHandler(APIChunk object, APIResponseStatus status) {
				// Check response
                if (status == APIResponseStatus.created) {
                    // Add new chunk to the list
                    getChunks().add(object);

                    // Call handler
                    handler.chunkLoaded(object);
                } else {
                    handler.chunkLoaded(null);
                }
			}
        });
    }

    // Unclaim a chunk
    public void unclaimChunk(Long x, Long z, final ChunkUnloaderHandler handler) {
        // Get chunk
        final APIChunk chunk = getChunk(x, z);
        if (chunk != null) {
            // Fetch API
            PopolServer.getInstance().getConnector().deleteChunk(x, z, new CompletionHandler<APIMessage>(){
                @Override
                public void completionHandler(APIMessage object, APIResponseStatus status) {
                    // Check response
                    if (status == APIResponseStatus.ok) {
                        // Remove chunk from region
                        getChunks().remove(chunk);
                    }

                    // Call handler in all cases
                    handler.chunkUnloaded();
                }
            });
        } else {
            // Not existing
            handler.chunkUnloaded();
        }
    }

    // Interface for region loading
    public interface RegionLoaderHandler {

        void regionLoaded(PopolRegion region);

    }

    // Interface for chunk loading
    public interface ChunkLoaderHandler {

        void chunkLoaded(APIChunk chunk);

    }

    // Interface for chunk lunoading
    public interface ChunkUnloaderHandler {

        void chunkUnloaded();

    }

}
