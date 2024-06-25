package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Config.FCMService;
import com.diplom.qrBackend.Models.News;
import com.diplom.qrBackend.Repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<News>> getAllNews() {
        List<News> newsList = newsRepository.findAll();
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable("id") Integer id) {
        Optional<News> news = newsRepository.findById(id);
        return news.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<News> createNews(@RequestBody News news) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedDateTime = currentTime.format(formatter);
        news.setPublishDateTime(LocalDateTime.parse(formattedDateTime, formatter));
        News savedNews = newsRepository.save(news);

        FCMService.sendNotificationToAllDevices(news.getTitle(), news.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<News> updateNews(@PathVariable("id") Integer id, @RequestBody News updatedNews) {
        Optional<News> optionalNews = newsRepository.findById(id);
        if (optionalNews.isPresent()) {
            updatedNews.setId(id);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime currentTime = LocalDateTime.now();
            String formattedDateTime = currentTime.format(formatter);
            updatedNews.setPublishDateTime(LocalDateTime.parse(formattedDateTime, formatter));

            News savedNews = newsRepository.save(updatedNews);

            return ResponseEntity.ok(savedNews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable("id") Integer id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
