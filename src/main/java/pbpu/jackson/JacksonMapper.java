package pbpu.jackson;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonMapper {
    
    private ObjectMapper objectMapper;

    private JacksonMapper(){
        this.objectMapper = new ObjectMapper();
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        this.objectMapper.setDefaultPrettyPrinter(prettyPrinter);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private ObjectMapper getMapper(){
        return this.objectMapper;
    }

    public static ObjectMapper getInstance(){
        return new JacksonMapper().getMapper();
    }
}
