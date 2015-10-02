package org.ethereum.sync;

import org.ethereum.net.server.Channel;
import org.ethereum.util.Functional;

import static org.ethereum.sync.SyncStateName.*;

/**
 * @author Mikhail Kalinin
 * @since 13.08.2015
 */
public class BlockRetrievingState extends AbstractSyncState {

    public BlockRetrievingState() {
        super(BLOCK_RETRIEVING);
    }

    @Override
    public void doOnTransition() {

        super.doOnTransition();

        syncManager.pool.changeState(BLOCK_RETRIEVING);
    }

    @Override
    public void doMaintain() {

        super.doMaintain();

        if (syncManager.queue.isHashesEmpty()) {
            syncManager.changeState(IDLE);
            return;
        }

        syncManager.pool.changeState(BLOCK_RETRIEVING, new Functional.Predicate<Channel>() {
            @Override
            public boolean test(Channel peer) {
                return peer.isIdle();
            }
        });
    }
}
