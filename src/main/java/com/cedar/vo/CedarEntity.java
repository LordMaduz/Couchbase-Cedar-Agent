package com.cedar.vo;

import cedarpolicy.value.Value;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CedarEntity {

    private CedarUid uid;
    private Map<String, Object> attrs;
    private List<CedarUid> parents;
}
