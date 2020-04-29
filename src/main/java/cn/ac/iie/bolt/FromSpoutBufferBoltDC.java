package cn.ac.iie.bolt;


import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class FromSpoutBufferBoltDC extends BaseBasicBolt {
    private static final long serialVersionUID = -106783017834081712L;

    private static Logger logger = Logger.getLogger(FromSpoutBufferBoltDC.class);

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        try {
            collector.emit(new Values(tuple.getString(0)));
        } catch (Exception e) {
            logger.error("FromSpoutBufferBoltDC Get Log is error --->" + e);
            e.printStackTrace();
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("json"));
    }

}
