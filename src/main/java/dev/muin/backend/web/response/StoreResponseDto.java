package dev.muin.backend.web.response;

import dev.muin.backend.domain.Stock.Stock;
import dev.muin.backend.domain.Store.Store;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StoreResponseDto {
    private String name;
    private List<String> keywords;
    private String address;
    private List<Stock> stocks;

    public StoreResponseDto(Store store) {
        this.name = store.getName();
        this.keywords = store.getKeywords().stream()
                .map(keyword -> keyword.name())
                .collect(Collectors.toList()); // TODO: 잘 가져오는지 확인
        this.address = store.getLocation().getAddress();
        this.stocks = store.getStocks();
    }

}
