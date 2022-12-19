package whu.vbs.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import whu.vbs.Entity.CsvFile.Query;

@Repository
public interface QueryMapper extends BaseMapper<Query> {
}
