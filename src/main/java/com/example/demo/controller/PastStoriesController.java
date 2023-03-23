import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Story;

@RestController
@RequestMapping("/past-stories")
public class PastStoriesController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping
    public List<Story> getPastStories() {
        Cache cache = cacheManager.getCache("top-stories");
        if (cache != null && cache.getNativeCache() instanceof ConcurrentMapCache) {
            ConcurrentMapCache nativeCache = (ConcurrentMapCache) cache.getNativeCache();
            return nativeCache.values().stream()
                    .flatMap(cacheValue -> ((List<Story>) cacheValue.get()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
