package com.github.duychuongvn.clocallback.service.impl;

import com.github.duychuongvn.clocallback.dao.entity.ScheduleInfo;
import com.github.duychuongvn.clocallback.dao.repository.ScheduleInfoRepository;
import com.github.duychuongvn.clocallback.service.CallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;

@Service
public class CallbackServiceImpl implements CallbackService {

    private Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);
    @Autowired
    private ScheduleInfoRepository scheduleInfoRepository;

    @Autowired
    private TransactionManager web3TransactionManager;

    @Value("${network.gasPrice}")
    private BigInteger gasPrice;

    @Override
    public void callbackContract(ScheduleInfo scheduleInfo) {
        if (!scheduleInfo.getFinished()) {

            logger.info("== Enter callback contract");

            try {
                EthSendTransaction result = web3TransactionManager.sendTransaction(gasPrice.multiply(Convert.Unit.GWEI.getWeiFactor().toBigIntegerExact()),
                        scheduleInfo.getGasLimit().multiply(BigInteger.TEN),
                        scheduleInfo.getContractAddress(),
                        inputData(scheduleInfo.getId()),
                        BigInteger.ZERO);
                if (result.getError() != null) {
                    logger.info("__callback error: queryid {} - error message {}", scheduleInfo.getId(), result.getError().getMessage());

                   // scheduleInfo.setFinished(true);
                    //scheduleInfoRepository.save(scheduleInfo);
                } else {
                    logger.info("__callback result: " + result.getTransactionHash());
                    scheduleInfo.setCallbackHash(result.getTransactionHash());
                    scheduleInfo.setFinished(true);
                    scheduleInfoRepository.save(scheduleInfo);
                }
            } catch (IOException e) {
                logger.error("-- Cannot callback to contract: " + e.getMessage(), e);
            }
        }

    }

    private String inputData(String queryId) {

        String strMethod = "dab23f7c";
        String strQueryId = stringTo64Symbols(Numeric.toHexString(Base64.getDecoder().decode(queryId)));
        return strMethod + strQueryId;
    }


    private static String stringTo64Symbols(String line) {
        if (line.charAt(0) == '0' && line.charAt(1) == 'x') {
            StringBuilder buffer = new StringBuilder(line);
            buffer.deleteCharAt(0);
            buffer.deleteCharAt(0);
            line = buffer.toString();
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("0000000000000000000000000000000000000000000000000000000000000000");

        for (int i = 0; i < line.length(); i++) {
            buffer.setCharAt(64 - i - 1, line.charAt(line.length() - i - 1));
        }
        return buffer.toString();

    }

}
