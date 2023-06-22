package org.nanjing.knightingal.processerlib;

import org.nanjing.knightingal.processerlib.beans.CounterBean;

/**
 * @author Knightingal
 * @since v1.0
 */


public interface RefreshListener {
    void doRefreshView(CounterBean counterBean);
}
