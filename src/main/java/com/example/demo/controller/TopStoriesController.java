import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Story;

@RestController
@RequestMapping("/top-stories")
@Cacheable("top-stories")
public class TopStoriesController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<Story> getTopStories() {
        Long[] storyIds = restTemplate.getForObject("https://hacker-news.firebaseio.com/v0/topstories.json", Long[].class);
        Instant fifteenMinutesAgo = Instant.now().minus(Duration.ofMinutes(15));
        return Arrays.stream(storyIds)
                .parallel()
                .map(id -> restTemplate.getForObject(String.format("https://hacker-news.firebaseio.com/v0/item/%d.json", id), Story.class))
                .filter(story -> story.getTime() > fifteenMinutesAgo.getEpochSecond())
                .sorted(Comparator.comparingInt(Story::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
