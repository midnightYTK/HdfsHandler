package org.hdfs.tools.mr.demo;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FlowCountReducer extends Reducer<Text, FlowBean, Text, FlowBean>{
	@Override
	protected void reduce(Text key, Iterable<FlowBean> values, 
			Reducer<Text, FlowBean, Text, FlowBean>.Context context)
					throws IOException, InterruptedException {
		
		int upFlowSum = 0;
		int downFlowSum = 0;
		int allSumFlow = 0;
		
		for( FlowBean fb : values ) {
			upFlowSum += fb.getUpFlow();
			downFlowSum += fb.getDownFlow();
		}
		allSumFlow = upFlowSum + downFlowSum;
		context.write(key, new FlowBean(key.toString(), upFlowSum, downFlowSum, allSumFlow));
		
	}
}
