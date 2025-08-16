package com.consolefire.xpenser.expense.service;

import com.consolefire.xpenser.expense.data.dto.query.AbstractQueryProperties;
import com.consolefire.xpenser.expense.data.dto.query.QueryRequestDto;
import java.util.List;
import java.util.Optional;

public interface QueryService<Q extends AbstractQueryProperties, R> {

    Optional<R> querySingleRecord(QueryRequestDto<Q> queryRequestDto);

    List<R> queryForList(QueryRequestDto<Q> queryRequestDto);

}
