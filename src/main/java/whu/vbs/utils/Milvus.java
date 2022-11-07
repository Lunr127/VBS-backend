package whu.vbs.utils;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.ReleaseCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Milvus {
    public static void query() {
        //建立连接
        final MilvusServiceClient milvusClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost("localhost")
                        .withPort(19530)
                        .build()
        );

        //准备数据
        Random ran = new Random();
        List<Long> book_id_array = new ArrayList<>();
        List<Long> word_count_array = new ArrayList<>();
        List<List<Float>> book_intro_array = new ArrayList<>();
        for (long i = 0L; i < 2000; ++i) {
            book_id_array.add(i);
            word_count_array.add(i + 10000);
            List<Float> vector = new ArrayList<>();
            for (int k = 0; k < 2; ++k) {
                vector.add(ran.nextFloat());
            }
            book_intro_array.add(vector);
        }

        //插入数据
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("book_id", book_id_array));
        fields.add(new InsertParam.Field("word_count", word_count_array));
        fields.add(new InsertParam.Field("book_intro", book_intro_array));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName("book")
                .withPartitionName("novel")
                .withFields(fields)
                .build();
        milvusClient.insert(insertParam);

        //索引参数
        final IndexType INDEX_TYPE = IndexType.IVF_FLAT;   // IndexType
        final String INDEX_PARAM = "{\"nlist\":1024}";     // ExtraParam

        //建立索引
        milvusClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withCollectionName("book")
                        .withFieldName("book_intro")
                        .withIndexType(INDEX_TYPE)
                        .withMetricType(MetricType.L2)
                        .withExtraParam(INDEX_PARAM)
                        .withSyncMode(Boolean.FALSE)
                        .build()
        );

        //加载数据至内存
        milvusClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withCollectionName("book")
                        .build()
        );

        //查询参数
        final Integer SEARCH_K = 2;                       // TopK
        final String SEARCH_PARAM = "{\"nprobe\":10}";    // Params

        //向量查询
        List<String> search_output_fields = Arrays.asList("book_id");
        List<List<Float>> search_vectors = Arrays.asList(Arrays.asList(0.1f, 0.2f));

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName("book")
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(MetricType.L2)
                .withOutFields(search_output_fields)
                .withTopK(SEARCH_K)
                .withVectors(search_vectors)
                .withVectorFieldName("book_intro")
                .withParams(SEARCH_PARAM)
                .build();
        R<SearchResults> respSearch = milvusClient.search(searchParam);

        //查询结果
        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
        System.out.println(wrapperSearch.getIDScore(0));
        System.out.println(wrapperSearch.getFieldData("book_id", 0));


        //从内存释放数据
        milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName("book")
                        .build());


        //释放连接
        milvusClient.close();

    }
}
