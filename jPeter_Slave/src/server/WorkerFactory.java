package server;

import globalDefinition.WorkerTypeDefinition;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import utils.WorkerRatioParser;
import warehouses.WorkRecordWareHouse;
import worker.ExampleCountDownWorker;
import worker.CountDownWorkerBase;

public class WorkerFactory {
	private int numOfZombie;
	
	public WorkerFactory(int numOfConcurrentZombie) {
		this.numOfZombie = numOfConcurrentZombie;
	}

	public CountDownWorkerBase spawnWorker(String type, int id, int serialPrefix, String atkTarget, CountDownLatch begin, CountDownLatch end, WorkRecordWareHouse recordWareHouse) {
		switch(type) {
		case WorkerTypeDefinition.EXAMPLE_COUNT_DOWN_WORKER:
			return new ExampleCountDownWorker(id, serialPrefix, atkTarget, begin, end, recordWareHouse);
		default:
			throw new RuntimeException("Type given does not match any worker type");
		}
	}
	
	public ArrayList<CountDownWorkerBase> spawnWorkers(String atkTarget, int serialPrefix, CountDownLatch begin, CountDownLatch end, WorkRecordWareHouse recordWareHouse) {
		String[] numerators = WorkerRatioParser.getNumerators();
		if(numerators.length != WorkerTypeDefinition.WORKER_TYPES.length)
			throw new RuntimeException("Size of worker ratio does not match number of types of worker");
		ArrayList<CountDownWorkerBase> workers = new ArrayList<CountDownWorkerBase>();
		
		int denominator = WorkerRatioParser.getDenominator();
		int numOfFixedNumOfWorkers = WorkerRatioParser.getNumOfFixedNumOfWorkers();
		
		int workerSerial = 0;
		for(int i = 0; i < numerators.length; i++) {
			// create EXACT number of workers for negative numbers
			if(Integer.parseInt(numerators[i]) < 0) {
				for(int j = 0; j > Integer.parseInt(numerators[i]); j--) {
					workers.add(spawnWorker(WorkerTypeDefinition.WORKER_TYPES[i], workerSerial++, serialPrefix, atkTarget, begin, end, recordWareHouse));
				}
					
			}
			// create workers according to given ratio for positive numbers
			if(Integer.parseInt(numerators[i]) > 0) {
				for(int j = 0; j < (Double.parseDouble(numerators[i]) * (numOfZombie - numOfFixedNumOfWorkers) / denominator/1.0); j++) {
					if(workers.size() < numOfZombie)
						workers.add(spawnWorker(WorkerTypeDefinition.WORKER_TYPES[i], workerSerial++, serialPrefix, atkTarget, begin, end, recordWareHouse));
				}
			}
		}
		
		return workers;
	}
}
