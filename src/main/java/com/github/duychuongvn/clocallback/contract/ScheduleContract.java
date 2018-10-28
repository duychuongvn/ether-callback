package com.github.duychuongvn.clocallback.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

import java.math.BigInteger;
import java.util.Arrays;


public class ScheduleContract extends Contract {
    private static final String BINARY = "0x60606040526004361061008e576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631d9b1d6f14610093578063455259cb146100d057806368742da6146100f95780637078aa28146101325780638da5cb5b1461013c578063b62a717c14610191578063c281d19e146101ca578063f2fde38b1461021f575b600080fd5b6100b26004808035906020019091908035906020019091905050610258565b60405180826000191660001916815260200191505060405180910390f35b34156100db57600080fd5b6100e3610462565b6040518082815260200191505060405180910390f35b341561010457600080fd5b610130600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061046c565b005b61013a610521565b005b341561014757600080fd5b61014f610598565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561019c57600080fd5b6101c8600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506105bd565b005b34156101d557600080fd5b6101dd61065c565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561022a57600080fd5b610256600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610682565b005b60008160008082600154029150813410151561045457813403905060008111156102b3573373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050505b624f1a0042018611806102c557504585115b156102cf57600080fd5b3033600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054604051808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166c010000000000000000000000000281526014018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166c01000000000000000000000000028152601401828152602001935050505060405180910390209350600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600081548092919060010191905055508584600019163373ffffffffffffffffffffffffffffffffffffffff167f72818f2dbb12787441f111a3d90afb9bd93a349e59f66eb5ee2fe37e754a6c20886040518082815260200191505060405180910390a4610459565b600080fd5b50505092915050565b6000600154905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156104c757600080fd5b8073ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f19350505050151561051e57600080fd5b50565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561057c57600080fd5b6402540be4003411151561058f57600080fd5b34600181905550565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561061857600080fd5b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156106dd57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561071957600080fd5b8073ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a3806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505600a165627a7a72305820b32c72b23c6d107e847f7e4f9828230e14d7b6342c7ea4c4b91ba4275e8ee8a40029";

    public ScheduleContract(Web3j web3j, TransactionManager transactionManager, String address, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, address, web3j, transactionManager, gasPrice, gasLimit);
    }


    public Observable<ScheduleEvent> approvalScheduleEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, this.getContractAddress());
        Event event = new Event("ScheduledEvent",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>() {},
                        new TypeReference<Bytes32>() {},
                        new TypeReference<Uint256>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        filter.addSingleTopic(EventEncoder.encode(event));


        return web3j.ethLogObservable(filter).map(new Func1<Log, ScheduleEvent>() {
            @Override
            public ScheduleEvent call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ScheduleEvent typedResponse = new ScheduleEvent();
                typedResponse.target = (Address)  eventValues.getIndexedValues().get(0);
                typedResponse.queryId = (Bytes32) eventValues.getIndexedValues().get(1);
                typedResponse.timestamp = (Uint256) eventValues.getIndexedValues().get(2);
                typedResponse.gasLimit = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }
    public static class ScheduleEvent {
        public Address target;
        public Bytes32 queryId;
        public Uint256 timestamp;
        public Uint256 gasLimit;
    }
}
