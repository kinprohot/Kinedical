package com.kinedical.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kinedical.model.HealthContent;
import com.kinedical.repository.HealthContentRepository;

@Service
public class HealthContentService {

    private final HealthContentRepository healthContentRepository;

    public HealthContentService(HealthContentRepository healthContentRepository) {
        this.healthContentRepository = healthContentRepository;
    }

    public List<HealthContent> findAll() {
        return healthContentRepository.findAll();
    }

    public Optional<HealthContent> findById(String id) {
        return healthContentRepository.findById(id);
    }

    public List<HealthContent> findPublished() {
        return healthContentRepository.findByStatusOrderByPublishDateDesc(HealthContent.ContentStatus.PUBLISHED);
    }

    public List<HealthContent> findByCategory(HealthContent.ContentCategory category) {
        return findPublished().stream()
                .filter(c -> c.getCategory() == category)
                .toList();
    }

    public HealthContent create(HealthContent content) {
        return healthContentRepository.save(content);
    }

    public HealthContent update(String id, HealthContent updatedContent) {
        return healthContentRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedContent.getTitle());
                    existing.setSlug(updatedContent.getSlug());
                    existing.setSummary(updatedContent.getSummary());
                    existing.setBody(updatedContent.getBody());
                    existing.setAuthorId(updatedContent.getAuthorId());
                    existing.setAuthorName(updatedContent.getAuthorName());
                    existing.setCategory(updatedContent.getCategory());
                    existing.setTags(updatedContent.getTags());
                    existing.setStatus(updatedContent.getStatus());
                    existing.setPublishDate(updatedContent.getPublishDate());
                    existing.setLanguage(updatedContent.getLanguage());
                    existing.setFeaturedImage(updatedContent.getFeaturedImage());
                    existing.setReadTimeMinutes(updatedContent.getReadTimeMinutes());
                    existing.setMeta(updatedContent.getMeta());
                    existing.setStats(updatedContent.getStats());
                    existing.setRelatedContentIds(updatedContent.getRelatedContentIds());
                    existing.setVector(updatedContent.getVector());
                    existing.setUpdatedAt(updatedContent.getUpdatedAt());
                    return healthContentRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("HealthContent not found: " + id));
    }

    public void delete(String id) {
        healthContentRepository.deleteById(id);
    }

    public void initializeDefaultArticles() {
        healthContentRepository.deleteAll(); // Xóa sạch dữ liệu cũ bị lệch/lỗi thời
        List<HealthContent> list = new java.util.ArrayList<>();
            
            // 1. NUTRITION
            HealthContent art1 = new HealthContent();
            art1.setTitle("Chế độ ăn Địa Trung Hải: Lợi ích vượt trội cho tim mạch");
            art1.setSlug("che-do-an-dia-trung-hai-tim-mach");
            art1.setSummary("Khám phá bí quyết dinh dưỡng từ chế độ ăn giàu dầu ô liu, cá, ngũ cốc nguyên hạt và rau quả tươi giúp bảo vệ hệ tim mạch.");
            art1.setBody("Chế độ ăn Địa Trung Hải được các nhà khoa học đánh giá là một trong những lối sống lành mạnh nhất hành tinh. " +
                    "Nó nhấn mạnh vào việc sử dụng chất béo không bão hòa đơn (như dầu ô liu nguyên chất), tăng cường ăn cá giàu omega-3, các loại hạt, " +
                    "ngũ cốc nguyên hạt và rau xanh trái cây tươi. Nhiều nghiên cứu lâm sàng đã chứng minh chế độ ăn này giúp giảm tới 30% nguy cơ mắc các bệnh tim mạch đột quỵ.");
            art1.setAuthorId("admin-001");
            art1.setAuthorName("BS. Nguyễn Văn An");
            art1.setCategory(HealthContent.ContentCategory.NUTRITION);
            art1.setTags(List.of("Dinh dưỡng", "Tim mạch", "Chế độ ăn"));
            art1.setStatus(HealthContent.ContentStatus.PUBLISHED);
            art1.setPublishDate(java.time.Instant.now());
            art1.setLanguage("vi");
            art1.setFeaturedImage("https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&q=80&w=400");
            art1.setReadTimeMinutes(5);
            art1.setCreatedAt(java.time.Instant.now());
            art1.setUpdatedAt(java.time.Instant.now());
            HealthContent.Stats stats1 = new HealthContent.Stats();
            stats1.setViews(120);
            stats1.setLikes(45);
            stats1.setCommentsCount(12);
            stats1.setShares(8);
            art1.setStats(stats1);
            art1.setVector(List.of(0.1, 0.2, 0.3));
            list.add(art1);

            // 2. DISEASE
            HealthContent art2 = new HealthContent();
            art2.setTitle("Kiểm soát đường huyết hiệu quả ở người bệnh Tiểu đường tuýp 2");
            art2.setSlug("kiem-soat-duong-huyet-tieu-duong");
            art2.setSummary("Các phương pháp tự nhiên và khoa học để duy trì chỉ số đường huyết ổn định, phòng ngừa các biến chứng nguy hiểm.");
            art2.setBody("Bệnh tiểu đường tuýp 2 ngày càng phổ biến trong xã hội hiện đại. Để chung sống hòa bình với căn bệnh này, bệnh nhân cần điều chỉnh chế độ ăn uống khoa học, " +
                    "hạn chế tinh bột hấp thu nhanh và đường tinh luyện. Đồng thời, việc duy trì vận động thể chất ít nhất 30 phút mỗi ngày và uống thuốc đúng liều lượng chỉ định " +
                    "là cốt lõi giúp phòng tránh biến chứng tim mạch, thận và mắt.");
            art2.setAuthorId("admin-001");
            art2.setAuthorName("BS. Trần Thị Bình");
            art2.setCategory(HealthContent.ContentCategory.DISEASE);
            art2.setTags(List.of("Bệnh lý", "Tiểu đường", "Sức khỏe"));
            art2.setStatus(HealthContent.ContentStatus.PUBLISHED);
            art2.setPublishDate(java.time.Instant.now());
            art2.setLanguage("vi");
            art2.setFeaturedImage("https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&q=80&w=400");
            art2.setReadTimeMinutes(6);
            art2.setCreatedAt(java.time.Instant.now());
            art2.setUpdatedAt(java.time.Instant.now());
            HealthContent.Stats stats2 = new HealthContent.Stats();
            stats2.setViews(95);
            stats2.setLikes(32);
            stats2.setCommentsCount(5);
            stats2.setShares(3);
            art2.setStats(stats2);
            art2.setVector(List.of(0.2, 0.1, 0.4));
            list.add(art2);

            // 3. EXERCISE
            HealthContent art3 = new HealthContent();
            art3.setTitle("5 bài tập Cardio tại nhà tốt nhất cho tuần hoàn máu");
            art3.setSlug("5-bai-tap-cardio-tuan-hoan-mau");
            art3.setSummary("Tăng cường sức bền, cải thiện tuần hoàn máu và đốt cháy calo hiệu quả với các bài tập không cần dụng cụ chuyên dụng.");
            art3.setBody("Tập luyện thể thao là nền tảng của một sức khỏe vàng. Với 5 bài tập Cardio đơn giản gồm Jumping Jacks, Burpees, Mountain Climbers, High Knees và squat jump, " +
                    "bạn hoàn toàn có thể tự luyện tập tại nhà mà không cần dụng cụ đắt tiền. Các bài tập này kích thích cơ tim co bóp nhịp nhàng, tối ưu lượng oxy trong máu và tăng tuần hoàn.");
            art3.setAuthorId("admin-001");
            art3.setAuthorName("HLV. Nguyễn Hoàng Nam");
            art3.setCategory(HealthContent.ContentCategory.EXERCISE);
            art3.setTags(List.of("Tập luyện", "Cardio", "Tại nhà"));
            art3.setStatus(HealthContent.ContentStatus.PUBLISHED);
            art3.setPublishDate(java.time.Instant.now());
            art3.setLanguage("vi");
            art3.setFeaturedImage("https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&q=80&w=400");
            art3.setReadTimeMinutes(4);
            art3.setCreatedAt(java.time.Instant.now());
            art3.setUpdatedAt(java.time.Instant.now());
            HealthContent.Stats stats3 = new HealthContent.Stats();
            stats3.setViews(150);
            stats3.setLikes(67);
            stats3.setCommentsCount(15);
            stats3.setShares(18);
            art3.setStats(stats3);
            art3.setVector(List.of(0.1, 0.3, 0.2));
            list.add(art3);

            // 4. MENTAL_HEALTH
            HealthContent art4 = new HealthContent();
            art4.setTitle("Thiền định và chánh niệm: Giải pháp đẩy lùi căng thẳng lo âu");
            art4.setSlug("thien-dinh-chanh-niem-giam-lo-au");
            art4.setSummary("Luyện tập chánh niệm mỗi ngày giúp tâm trí bình yên, giảm lo âu và nâng cao hiệu suất làm việc.");
            art4.setBody("Căng thẳng và áp lực từ cuộc sống hiện đại tàn phá thầm lặng hệ thần kinh của bạn. Thiền định và chánh niệm (Mindfulness) giúp bạn quay trở lại giây phút hiện tại, " +
                    "thả lỏng các nhóm cơ căng cứng, điều hòa hơi thở sâu và chậm lại. Thực hành thiền định 10 phút mỗi sáng sẽ tái tạo năng lượng tinh thần tích cực mạnh mẽ.");
            art4.setAuthorId("admin-001");
            art4.setAuthorName("BS. Lê Hoàng Nam");
            art4.setCategory(HealthContent.ContentCategory.MENTAL_HEALTH);
            art4.setTags(List.of("Tâm thần", "Thiền định", "Chánh niệm"));
            art4.setStatus(HealthContent.ContentStatus.PUBLISHED);
            art4.setPublishDate(java.time.Instant.now());
            art4.setLanguage("vi");
            art4.setFeaturedImage("https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&q=80&w=400");
            art4.setReadTimeMinutes(5);
            art4.setCreatedAt(java.time.Instant.now());
            art4.setUpdatedAt(java.time.Instant.now());
            HealthContent.Stats stats4 = new HealthContent.Stats();
            stats4.setViews(180);
            stats4.setLikes(80);
            stats4.setCommentsCount(24);
            stats4.setShares(25);
            art4.setStats(stats4);
            art4.setVector(List.of(0.3, 0.1, 0.2));
            list.add(art4);

            // 5. PREVENTION
            HealthContent art5 = new HealthContent();
            art5.setTitle("Khám sức khỏe định kỳ: Chiếc chìa khóa vàng cho tuổi thọ");
            art5.setSlug("kham-suc-khoe-dinh-ky-suc-khoe");
            art5.setSummary("Vì sao chủ động tầm soát bệnh tật sớm là giải pháp bền vững bảo vệ chất lượng cuộc sống lâu dài.");
            art5.setBody("Chủ động phòng bệnh hơn chữa bệnh. Nhiều căn bệnh âm thầm tiến triển mà không hề gây triệu chứng đau đớn ở giai đoạn khởi phát. " +
                    "Khám sức khỏe tổng quát định kỳ ít nhất 1 lần mỗi năm giúp tầm soát phát hiện sớm các bệnh mạn tính nguy hiểm, " +
                    "từ đó bác sĩ đưa ra phác đồ can thiệp kịp thời bảo vệ cơ thể trọn vẹn.");
            art5.setAuthorId("admin-001");
            art5.setAuthorName("BS. Phạm Minh Đức");
            art5.setCategory(HealthContent.ContentCategory.PREVENTION);
            art5.setTags(List.of("Phòng bệnh", "Khám định kỳ", "Tầm soát"));
            art5.setStatus(HealthContent.ContentStatus.PUBLISHED);
            art5.setPublishDate(java.time.Instant.now());
            art5.setLanguage("vi");
            art5.setFeaturedImage("https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&q=80&w=400");
            art5.setReadTimeMinutes(5);
            art5.setCreatedAt(java.time.Instant.now());
            art5.setUpdatedAt(java.time.Instant.now());
            HealthContent.Stats stats5 = new HealthContent.Stats();
            stats5.setViews(110);
            stats5.setLikes(40);
            stats5.setCommentsCount(8);
            stats5.setShares(6);
            art5.setStats(stats5);
            art5.setVector(List.of(0.2, 0.2, 0.2));
            list.add(art5);

            healthContentRepository.saveAll(list);
    }
}
