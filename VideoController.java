
@Controller
public class VideoController {

    @Inject
    private YoutubeVideoService videoService;

    @Inject
    private SpecialListService listService;

    @Value("${view.admin.video.per_page_count}")
    private Integer perPageCount;

    @Value("${view.admin.video.page_sort}")
    private String pageSort;

    private Logger logger = LoggerFactory.getLogger(VideoController.class);

    @RequestMapping(value = "/demo/videos/count", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getVideoCount() {
        return new ResponseEntity<>(videoService.getTotalCount(), HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/pageinfo", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageInfo> getPageInfo() {
        int totalVideos = videoService.getTotalCount().intValue();
        int totalPages = totalVideos/perPageCount + (totalVideos%perPageCount > 0 ? 1 : 0);
        PageInfo info = new PageInfo(totalVideos,totalPages,perPageCount,1);
        return new ResponseEntity<>(info,HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/pageinfo/name/{searchTerm}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageInfo> getPageInfoByName(@PathVariable("searchTerm") String name) {
        int totalVideos = videoService.getTotalCountByNameLike(name).intValue();
        int totalPages = totalVideos/perPageCount + (totalVideos%perPageCount > 0 ? 1 : 0);
        PageInfo info = new PageInfo(totalVideos,totalPages,perPageCount,1);
        return new ResponseEntity<>(info,HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/pageinfo", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageInfo> getPageInfo(@RequestBody VideoFilterDTO filterDTO) {
        int totalVideos = videoService.getFilteredTotalCount(filterDTO).intValue();
        int totalPages = totalVideos/perPageCount + (totalVideos%perPageCount > 0 ? 1 : 0);
        PageInfo info = new PageInfo(totalVideos,totalPages,perPageCount,1);
        return new ResponseEntity<>(info,HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/{pageNum}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<YoutubeVideo>> getVideos(@PathVariable("pageNum") Integer page) {
        int pageNum = page - 1;
        if(page == null || page < 0) {
            pageNum = 0;
        }
        return new ResponseEntity<>(videoService.getAllYoutubeVideos(pageNum,perPageCount,pageSort),HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/search/{pageNum}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<YoutubeVideo>> getFilteredVideos(@PathVariable("pageNum") Integer page, @RequestBody VideoFilterDTO filterDTO) {
        int pageNum = page - 1;
        if(page == null || page < 0) {
            pageNum = 0;
        }

        return new ResponseEntity<>(videoService.getByFilter(filterDTO,pageNum,perPageCount,pageSort),HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/search/name/{searchTerm}/{pageNum}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<YoutubeVideo>> getFilteredVideosByName(@PathVariable("searchTerm") String name, @PathVariable("pageNum") Integer page) {
        int pageNum = page - 1;
        if(page == null || page < 0) {
            pageNum = 0;
        }

        return new ResponseEntity<>(videoService.getAllByName(name,pageNum,perPageCount,pageSort),HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<YoutubeVideo> saveVideo(@RequestBody YoutubeVideo vid) { //Changing from dto to model
        if(videoService.videoByYoutubeIdExists(vid.getYoutubeId())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        YoutubeVideo video = videoService.persist(vid);
        return new ResponseEntity<>(video,HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateVideo(@PathVariable("id") String id, @RequestBody YoutubeVideo vid) {
        if(!videoService.videoExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        vid.setId(id); //just to be sure!
        videoService.persist(vid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVideo(@PathVariable("id") String id) {
        if(!videoService.videoExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        videoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/demo/videos/carousel/id/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<YoutubeVideo>> getAllVideosForCarouselById(@PathVariable("id") String id) {

        return new ResponseEntity<>(videoService.getAllInCarouselByCarouselIdSorted(id), HttpStatus.OK);
    }
}
