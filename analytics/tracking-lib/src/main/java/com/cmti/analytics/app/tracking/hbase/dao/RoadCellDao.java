package com.cmti.analytics.app.tracking.hbase.dao;
 
import java.io.IOException;
import java.util.List;

import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
import com.cmti.analytics.hbase.dao.DaoScanner;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.util.ByteUtil;
 

/**
 * Dao for 'RoadCell'
 * @author Guobiao Mo
 *
 */
public class RoadCellDao extends HBaseGenericDao<RoadCell, Object> {
	public List<RoadCell> getRoadCellsByRoadId(int roadId) throws IOException{
		DaoScanner<RoadCell> daoScanner= getByKeyRange(ByteUtil.toBytes(roadId), ByteUtil.toBytes(roadId+1));	
		List<RoadCell> ret = daoScanner.next(1000000);//assume no road has more than this number of cells.
		return ret;
	}
}
