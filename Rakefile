task default: %[build]

desc "Build project"
task :build do
    puts "Building app container ->"
    sh "docker-compose -f docker-compose.yml run --rm --no-deps app yarn"
    puts "Building API container ->"
    sh "docker-compose build api"
end

desc "Log in to container shell"
task :console do
    if ARGV.count === 2 && ['app', 'api'].include?(ARGV[1])
        puts "Logging into #{ARGV[1]} container shell ->"
        sh "docker-compose exec #{ARGV[1]} /bin/bash"
    else
        puts "Usage ->"
        puts "rake console app -> login to app container shell"
        puts "rake console api -> login to API container shell"
    end
end

desc "Run servers"
task :server do
    puts "Starting app server on port 9555 and API server on port 7000 ->"
    sh "docker-compose up"
end

desc "Configure Tmuxinator"
task :tmux do
    puts "Configuring Tmuxinator ->"
    tmuxinator_dir = File.expand_path(Dir.home + "/.tmuxinator")
    if File.exist?(tmuxinator_dir)
        sh "ln -s #{Dir.pwd}/tmux.yml #{tmuxinator_dir}/geotrellis-research.yml"
    else
        puts "Tmuxinator not installed"
    end
end
